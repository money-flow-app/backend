package cm.bogne_stanley.money_flow.presentation.dto.request.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @JsonAlias("old_password")
        @NotBlank(message = "The field old password is required")
        String oldPassword,
        @JsonAlias("new_password")
        @NotBlank(message = "The field new password is required")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String newPassword
) {
}
