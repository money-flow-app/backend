package cm.bogne_stanley.money_flow.presentation.dto.response.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryStats(
    @JsonProperty("category_id")
    Long categoryId,
    @JsonProperty("category_name")
    String categoryName,
    @JsonProperty("total_amount")
    Double totalAmount,
    Long count,
    Double percentage
) {
}

