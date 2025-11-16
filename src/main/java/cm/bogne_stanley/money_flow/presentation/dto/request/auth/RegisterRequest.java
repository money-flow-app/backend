package cm.bogne_stanley.money_flow.presentation.dto.request.auth;

import org.springframework.security.crypto.password.PasswordEncoder;

import cm.bogne_stanley.money_flow.data.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank(message = "Last name must not be blank")
    String lastname,
    @NotBlank(message = "First name must not be blank")
    String firtname,
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    String email,
    @NotBlank(message = "Password must not be blank")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    String password
) {
    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
            .lastname(this.lastname)
            .firstname(this.firtname)
            .email(this.email)
            .password(passwordEncoder.encode(this.password))
            .build();
    }
}
