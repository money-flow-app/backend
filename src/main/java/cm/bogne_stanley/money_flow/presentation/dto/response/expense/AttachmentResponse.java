package cm.bogne_stanley.money_flow.presentation.dto.response.expense;

public record AttachmentResponse(
    Long id,
    String path,
    // String name,
    String extension
    // @JsonProperty("created_at")
    // Instant createdAt,
    // @JsonProperty("updated_at")
    // Instant updatedAt
) {
    
}
