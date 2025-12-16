package cm.bogne_stanley.money_flow.presentation.dto.response.stats;

import java.util.List;

public record CompareStatsResponse(
    List<PeriodStats> periods
) {
    public record PeriodStats(
        PeriodInfo period,
        StatsResponse stats
    ) {
    }
}

