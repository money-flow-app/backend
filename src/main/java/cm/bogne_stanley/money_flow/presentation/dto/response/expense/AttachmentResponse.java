package cm.bogne_stanley.money_flow.presentation.dto.response.expense;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AttachmentResponse(
    Long id,
    String path,
    String name,
    String extension,
    @JsonProperty("created_at")
    Instant createdAt,
    @JsonProperty("updated_at")
    Instant updatedAt
) {
    
}
