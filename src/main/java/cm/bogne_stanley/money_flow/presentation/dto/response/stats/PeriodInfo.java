package cm.bogne_stanley.money_flow.presentation.dto.response.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PeriodInfo(
    @JsonProperty("start_date")
    String startDate,
    @JsonProperty("end_date")
    String endDate
) {
}

