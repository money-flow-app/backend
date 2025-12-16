package cm.bogne_stanley.money_flow.presentation.api_controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.bogne_stanley.money_flow.common.wrappers.APIResponse;
import cm.bogne_stanley.money_flow.common.wrappers.PaginatedData;
import cm.bogne_stanley.money_flow.domain.service.ExpenseService;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.AddAttachmentRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.ExpenseRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.GetExpenseFilter;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.RemoveAttachmentRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.expense.ExpenseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<APIResponse<PaginatedData<ExpenseResponse>>> getExpenses(@Valid @ModelAttribute GetExpenseFilter filter, @PageableDefault(size = 3, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new APIResponse<>(true, "Expenses fetched successfully", expenseService.getExpenses(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ExpenseResponse>> getExpense(@PathVariable Long id) {
        return ResponseEntity.ok(new APIResponse<>(true, "Expense fetched successfully", expenseService.getExpense(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<ExpenseResponse>> createExpense(@Valid @ModelAttribute ExpenseRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Expense created successfully", expenseService.createExpense(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ExpenseResponse>> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Expense updated successfully", expenseService.updateExpense(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Expense deleted successfully", null));
    }
    
    @PostMapping(path = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<ExpenseResponse>> addAttachments(@PathVariable Long id, @Valid @ModelAttribute AddAttachmentRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Attachments added successfully", expenseService.addAttachments(id, request)));
    }
    @DeleteMapping("/{id}/attachments")
    public ResponseEntity<APIResponse<ExpenseResponse>> removeAttachments(@PathVariable Long id, @Valid @RequestBody RemoveAttachmentRequest request) {
        return ResponseEntity.ok(new APIResponse<>(true, "Attachments removed successfully", expenseService.bulkDeleteAttachment(id, request)));
    }
}
