package cm.bogne_stanley.money_flow.presentation.api_controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.bogne_stanley.money_flow.common.wrappers.APIResponse;
import cm.bogne_stanley.money_flow.domain.service.StatsService;
import cm.bogne_stanley.money_flow.presentation.dto.request.stats.CompareStatsRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.stats.StatsFilter;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.CompareStatsResponse;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.StatsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<APIResponse<StatsResponse>> getStats(@Valid @ModelAttribute StatsFilter filter) {
        return ResponseEntity.ok(new APIResponse<>(true, "Statistics fetched successfully", statsService.getStats(filter)));
    }

    @PostMapping("/compare")
    public ResponseEntity<APIResponse<CompareStatsResponse>> compareStats(@Valid @RequestBody CompareStatsRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Statistics compared successfully", statsService.compareStats(request)));
    }
}

