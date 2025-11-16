package cm.bogne_stanley.money_flow.presentation.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

public record RefreshTokenRequest(
        @JsonAlias("refresh_token")
        String refreshToken
) {
}
