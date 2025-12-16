package cm.bogne_stanley.money_flow.presentation.dto.response.expense;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import cm.bogne_stanley.money_flow.data.entity.ExpenseFrequency;
import cm.bogne_stanley.money_flow.data.entity.ExpenseType;
import cm.bogne_stanley.money_flow.presentation.dto.response.category.CategoryResponse;

public record ExpenseResponse(
    Long id,
    String title,
    Double amount,
    ExpenseType type,
    ExpenseFrequency frequency,
    @JsonProperty("next_payment_date")
    Instant nextPaymentDate,
    @JsonProperty("paid_date")
    Instant paidDate,
    String description,
    String note,
    List<AttachmentResponse> attachments,
    CategoryResponse category,
    @JsonProperty("created_at")
    Instant createdAt,
    @JsonProperty("updated_at")
    Instant updatedAt
) {
    
}
