package cm.bogne_stanley.money_flow.domain.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.CustomValidationException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.data.entity.Expense;
import cm.bogne_stanley.money_flow.data.entity.ExpenseFrequency;
import cm.bogne_stanley.money_flow.data.entity.ExpenseType;
import cm.bogne_stanley.money_flow.data.entity.User;
import cm.bogne_stanley.money_flow.data.repository.CategoryRepository;
import cm.bogne_stanley.money_flow.data.repository.ExpenseRepository;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.ExpenseRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryService;
    private final AttachmentService attachmentService;

    @SuppressWarnings("null")
    public Expense createExpense(ExpenseRequest expenseRequest) {
        var expense = handleExpense(new Expense(), expenseRequest);
        return expenseRepository.save(expense);
    }

    @SuppressWarnings("null")
    public Expense updateExpense(Long id, ExpenseRequest expenseRequest) {
        var existingExpense = expenseRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        var updatedExpense = handleExpense(existingExpense, expenseRequest);
        return expenseRepository.save(updatedExpense);
    }

    @SuppressWarnings("null")
    public void deleteExpense(Long id) {
        var existingExpense = expenseRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        attachmentService.deleteAttachmentsByExpense(existingExpense);
        expenseRepository.delete(existingExpense);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
    }

    public Expense bulkDeleteAttachment(Long expenseId, List<Long> attachmentIds) {
        var expense = expenseRepository.findByIdAndUser(expenseId, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        
        var attachments = expense.getAttachments().stream()
            .filter(attachment -> attachmentIds.contains(attachment.getId()))
            .toList();

        attachmentService.deleteAttachmentsByIds(attachments.stream().map(a -> a.getId()).toList());

        expense.getAttachments().removeAll(attachments);

        return expenseRepository.save(expense);
    }

    public Expense addAttachment(Long expenseId,  List<MultipartFile> file) {
        var expense = expenseRepository.findByIdAndUser(expenseId, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        
        var attachments = attachmentService.bulkCreateAttachments(expense, file);
        expense.getAttachments().addAll(attachments);
        
        return expenseRepository.save(expense);
    }

    public Expense markExpenseAsDone(Long id) {
        var expense = expenseRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        
        expense.setType(ExpenseType.DONE);
        expense.setPaidDate(Instant.now());
        
        return expenseRepository.save(expense);
    }

    private void validateExpense(Expense expense) {
        if (expense.getType().equals(ExpenseType.RECURRING) && expense.getFrequency() == null) {
            throw new CustomValidationException("frequency", "Frequency is required for RECURRING expenses");
        }
        if ((expense.getType().equals(ExpenseType.RECURRING) || expense.getType().equals(ExpenseType.FIXED)) && expense.getNextPaymentDate() == null) {
            throw new CustomValidationException("next_payment_date", "Next Payment Date is required for RECURRING and FIXED expenses");
        }
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Expense handleExpense(Expense expense, ExpenseRequest expenseRequest) {
        expense.setTitle(expenseRequest.title());
        expense.setAmount(expenseRequest.amount());
        expense.setType(ExpenseType.valueOf(expenseRequest.type()));
        expense.setDescription(expenseRequest.description());
        expense.setNote(expenseRequest.note());
        if (expense.getUser() == null) {
            expense.setUser(getCurrentUser());
        }

        if (expenseRequest.frequency() != null && expense.getType().equals(ExpenseType.RECURRING)) {
            expense.setFrequency(ExpenseFrequency.valueOf(expenseRequest.frequency()));
        }

        if (expenseRequest.nextPaymentDate() != null && (expense.getType().equals(ExpenseType.RECURRING) || expense.getType().equals(ExpenseType.FIXED))) {
            expense.setNextPaymentDate(expenseRequest.nextPaymentDate().atZone(ZoneOffset.UTC).toInstant());
        }
        
        validateExpense(expense);

        if (expense.getType().equals(ExpenseType.DONE)) {
            expense.setPaidDate(expenseRequest.paidDate() == null ? Instant.now() : expenseRequest.paidDate().atZone(ZoneOffset.UTC).toInstant());
        }
        
        if (expenseRequest.categoryId() != null) {
            @SuppressWarnings("null")
            var category = categoryService.findById(expenseRequest.categoryId()).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            expense.setCategory(category);
        } else {
            expense.setCategory(null);
        }

        if (expenseRequest.attachments() != null) {
            var attachments = attachmentService.bulkCreateAttachments(expense, expenseRequest.attachments());
            expense.setAttachments(attachments);
        }
        return expense;

    }

}
