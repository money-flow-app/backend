package cm.bogne_stanley.money_flow.domain.service;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.presentation.dto.UserDto;
import cm.bogne_stanley.money_flow.presentation.dto.request.user.ResetPasswordRequest;

import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.bogne_stanley.money_flow.data.entity.RefreshToken;
import cm.bogne_stanley.money_flow.data.entity.User;
import cm.bogne_stanley.money_flow.data.repository.RefreshTokenRepository;
import cm.bogne_stanley.money_flow.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
    }

    public User save(@NonNull User user){
        return userRepository.save(user);
    }

    public String resetPassword(ResetPasswordRequest request){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())){
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

        return "Your password has been updated successfully";
    }

    @Transactional
    public String logout(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        RefreshToken token = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if(token == null){
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        refreshTokenRepository.delete(token);

        return "You have been logged out successfully";
    }

    public UserDto getMe() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserDto.fromEntity(user);
    }
}
