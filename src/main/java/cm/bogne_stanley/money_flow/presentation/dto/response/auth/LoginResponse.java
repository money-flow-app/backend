package cm.bogne_stanley.money_flow.presentation.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import cm.bogne_stanley.money_flow.presentation.dto.UserDto;


public record LoginResponse(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("refresh_token")
    String refreshToken,
    UserDto user
) { }
