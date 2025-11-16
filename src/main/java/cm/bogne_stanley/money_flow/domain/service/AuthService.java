package cm.bogne_stanley.money_flow.domain.service;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.common.services.MailService;
import cm.bogne_stanley.money_flow.data.entity.ActivationCode;
import cm.bogne_stanley.money_flow.data.entity.RefreshToken;
import cm.bogne_stanley.money_flow.data.entity.User;
import cm.bogne_stanley.money_flow.data.repository.ActivationCodeRepository;
import cm.bogne_stanley.money_flow.data.repository.RefreshTokenRepository;
import cm.bogne_stanley.money_flow.data.repository.UserRepository;
import cm.bogne_stanley.money_flow.domain.model.RefreshTokenModel;
import cm.bogne_stanley.money_flow.presentation.dto.UserDto;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.LoginRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.RefreshTokenRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.RegisterRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.ResendActivationCodeRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.ValidateCodeRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.auth.LoginResponse;
import cm.bogne_stanley.money_flow.security.TokenUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    // Constantes
    private static final int ACTIVATION_CODE_LENGTH = 6;
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    // Dépendances
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final MailService mailService;
    
    @Transactional
    public LoginResponse login(LoginRequest request){
        Optional<User> optionalUser = userRepository.findByEmail(request.email());
        if(optionalUser.isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        User user = optionalUser.get();
        
        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        if(!user.isActive()){
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        return createLoginResponse(user);
    }

    @Transactional
    @SuppressWarnings("null")
    public String register(RegisterRequest request){
        User newUser = request.toUser(passwordEncoder);
        User user = userRepository.save(newUser);

        ActivationCode activationCode = ActivationCode.builder()
                .code(generateActivationCode())
                .user(user)
                .build();

        activationCodeRepository.save(activationCode);
        
        return "A validation code has been sent to " + user.getEmail();
    }

    @Transactional
    public LoginResponse validateCode(ValidateCodeRequest request){
        ActivationCode code = activationCodeRepository.findByCode(request.code())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CODE));
        
        if(code.isUsed()){
            throw new BusinessException(ErrorCode.CODE_ALREADY_USED);
        }
        
        if(code.getExpiryDate().isBefore(Instant.now())){
            throw new BusinessException(ErrorCode.CODE_EXPIRED);
        }
        
        // Activation du code et du compte utilisateur
        code.setUsed(true);
        activationCodeRepository.save(code);
        
        User user = code.getUser();
        user.setActive(true);
        userRepository.save(user);

        return createLoginResponse(user);
    }

    @Transactional
    public LoginResponse processToRefreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if(token.getExpiryDate().isBefore(Instant.now())){
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        // Suppression de l'ancien token
        refreshTokenRepository.delete(token);

        // Création d'une nouvelle session
        User user = token.getUser();
        return createLoginResponse(user);
    }

    
    @Transactional
    public String resendActivationCode(ResendActivationCodeRequest request){
        // Vérification de l'existence de l'utilisateur
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND));

        if (user.isActive()) {
            throw new BusinessException(ErrorCode.CODE_ALREADY_USED);
        }

        // Trouver un code d'activation existant non utilisé et non expiré, sinon en régénérer un
        Optional<ActivationCode> optionalCode = activationCodeRepository.findByUser(user)
            .filter(code -> !code.isUsed() && code.getExpiryDate().isAfter(Instant.now()));

        
        ActivationCode codeToSend;
        if (optionalCode.isPresent()) {
            codeToSend = optionalCode.get();
        } else {
            String codeValue = generateActivationCode();
            codeToSend = ActivationCode.builder()
                .code(codeValue)
                .user(user)
                .build();

            if(codeToSend == null){
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to save activation code");
            }

            activationCodeRepository.save(codeToSend);
        }

        mailService.sendActivationCode(user.getEmail(), codeToSend.getCode());

        return "The activation code has been sent to your email address.";
    }

    private LoginResponse createLoginResponse(User user) {
        String accessToken = tokenUtils.createToken(user.getEmail());
        RefreshTokenModel refreshTokenModel = tokenUtils.createRefreshToken();
        RefreshToken savedToken = saveRefreshToken(user, refreshTokenModel);
        
        return new LoginResponse(accessToken, savedToken.getToken(), UserDto.fromEntity(user));
    }

    /**
     * Sauvegarde un refresh token pour un utilisateur
     */
    private RefreshToken saveRefreshToken(User user, RefreshTokenModel refreshToken){
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(refreshToken.token())
                .expiryDate(refreshToken.expiryDate())
                .user(user)
                .build();

        if(newRefreshToken == null){
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to save refresh token");
        }

        return refreshTokenRepository.save(newRefreshToken);
    }

    /**
     * Génère un code d'activation aléatoire sécurisé
     */
    private String generateActivationCode(){
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder(ACTIVATION_CODE_LENGTH);

        for (int i = 0; i < ACTIVATION_CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(CODE_CHARACTERS.length());
            code.append(CODE_CHARACTERS.charAt(index));
        }

        return code.toString();
    }

}
