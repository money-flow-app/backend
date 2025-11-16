package cm.bogne_stanley.money_flow.presentation.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ValidateCodeRequest(
    @NotBlank(message = "Code must not be blank")
    String code
) {
    
}
