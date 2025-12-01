package cm.bogne_stanley.money_flow.presentation.api_controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.bogne_stanley.money_flow.common.wrappers.APIResponse;
import cm.bogne_stanley.money_flow.domain.service.ExpenseService;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.ExpenseRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.expense.ExpenseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<ExpenseResponse>> createExpense(@Valid @ModelAttribute ExpenseRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Expense created successfully", expenseService.createExpense(request)));
    }
    
}
