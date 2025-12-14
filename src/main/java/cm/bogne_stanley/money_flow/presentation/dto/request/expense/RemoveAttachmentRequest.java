package cm.bogne_stanley.money_flow.presentation.dto.request.expense;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RemoveAttachmentRequest(
    @NotEmpty(message = "Attachment IDs are required")
    @Size(min = 1, message = "At least one attachment ID is required")
    @JsonAlias("attachment_ids")
    @JsonProperty("attachment_ids")
    List<Long> attachmentIds
) {
    
}
