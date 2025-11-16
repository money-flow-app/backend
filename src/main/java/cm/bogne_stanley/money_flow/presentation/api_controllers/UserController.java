package cm.bogne_stanley.money_flow.presentation.api_controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.bogne_stanley.money_flow.common.wrappers.APIResponse;
import cm.bogne_stanley.money_flow.domain.service.UserService;
import cm.bogne_stanley.money_flow.presentation.dto.UserDto;
import cm.bogne_stanley.money_flow.presentation.dto.request.user.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<APIResponse<UserDto>> getMe() {
        return ResponseEntity.ok(new APIResponse<>(true, "User retrieved", userService.getMe()));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Password reset", userService.resetPassword(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>> logout() {
        return ResponseEntity.ok(new APIResponse<>(true, "Logout successful", userService.logout()));
    }
}
