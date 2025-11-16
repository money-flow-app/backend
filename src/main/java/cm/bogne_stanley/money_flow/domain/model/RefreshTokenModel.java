package cm.bogne_stanley.money_flow.domain.model;

import java.time.Instant;

public record RefreshTokenModel(
    String token,
    Instant expiryDate
) {
    
}
