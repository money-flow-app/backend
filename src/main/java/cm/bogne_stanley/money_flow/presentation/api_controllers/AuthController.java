package cm.bogne_stanley.money_flow.presentation.api_controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.bogne_stanley.money_flow.common.wrappers.APIResponse;
import cm.bogne_stanley.money_flow.domain.service.AuthService;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.LoginRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.RefreshTokenRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.RegisterRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.ResendActivationCodeRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.auth.ValidateCodeRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.auth.LoginResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponse>> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(new APIResponse<>(true, "Login successful", authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(new APIResponse<>(true, "Registration successful", authService.register(request)));
    }

    @PostMapping("/resend-activation-code")
    public ResponseEntity<APIResponse<String>> resendActivationCode(@RequestBody ResendActivationCodeRequest request){
        return ResponseEntity.ok(new APIResponse<>(true, "Activation code resent", authService.resendActivationCode(request)));
    }

    @PostMapping("/validate-code")
    public ResponseEntity<APIResponse<LoginResponse>> validateCode(@RequestBody ValidateCodeRequest request){
        return ResponseEntity.ok(new APIResponse<>(true, "Code validated", authService.validateCode(request)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<APIResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(new APIResponse<>(true, "Token refreshed", authService.processToRefreshToken(request)));
    }
}
