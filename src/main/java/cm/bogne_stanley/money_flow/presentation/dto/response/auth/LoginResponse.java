package cm.bogne_stanley.money_flow.presentation.dto.response.auth;

import cm.bogne_stanley.money_flow.presentation.dto.UserDto;


public record LoginResponse(
    String accessToken,
    String refreshToken,
    UserDto user
) { }
