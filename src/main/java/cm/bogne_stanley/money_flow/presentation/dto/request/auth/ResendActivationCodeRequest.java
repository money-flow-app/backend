package cm.bogne_stanley.money_flow.presentation.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendActivationCodeRequest(
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    String email
) {
    
}
