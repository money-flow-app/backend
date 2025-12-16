package cm.bogne_stanley.money_flow.presentation.dto.response.stats;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StatsResponse(
    @JsonProperty("total_amount")
    Double totalAmount,
    @JsonProperty("total_count")
    Long totalCount,
    @JsonProperty("average_amount")
    Double averageAmount,
    @JsonProperty("by_category")
    List<CategoryStats> byCategory,
    @JsonProperty("by_type")
    List<TypeStats> byType,
    PeriodInfo period,
    EvolutionStats evolution
) {
}

