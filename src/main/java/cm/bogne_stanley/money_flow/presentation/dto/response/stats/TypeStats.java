package cm.bogne_stanley.money_flow.presentation.dto.response.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

import cm.bogne_stanley.money_flow.data.entity.ExpenseType;

public record TypeStats(
    ExpenseType type,
    @JsonProperty("total_amount")
    Double totalAmount,
    Long count,
    Double percentage
) {
}

