package cm.bogne_stanley.money_flow.presentation.dto.response.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EvolutionStats(
    @JsonProperty("change_percentage")
    Double changePercentage,
    @JsonProperty("change_amount")
    Double changeAmount
) {
}

