package cm.bogne_stanley.money_flow.presentation.dto;

import cm.bogne_stanley.money_flow.data.entity.User;

public record UserDto(
    Long id,
    String firstname,
    String lastname,
    String email
) {
    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail()
        );
    }
}