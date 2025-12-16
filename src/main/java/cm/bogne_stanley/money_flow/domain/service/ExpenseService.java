package cm.bogne_stanley.money_flow.domain.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.CustomValidationException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.common.mappers.ExpenseMapper;
import cm.bogne_stanley.money_flow.common.mappers.PaginationMapper;
import cm.bogne_stanley.money_flow.common.wrappers.PaginatedData;
import cm.bogne_stanley.money_flow.data.entity.Attachment;
import cm.bogne_stanley.money_flow.data.entity.Expense;
import cm.bogne_stanley.money_flow.data.entity.ExpenseFrequency;
import cm.bogne_stanley.money_flow.data.entity.ExpenseType;
import cm.bogne_stanley.money_flow.data.entity.User;
import cm.bogne_stanley.money_flow.data.repository.CategoryRepository;
import cm.bogne_stanley.money_flow.data.repository.ExpenseRepository;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.AddAttachmentRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.ExpenseRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.GetExpenseFilter;
import cm.bogne_stanley.money_flow.presentation.dto.request.expense.RemoveAttachmentRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.expense.ExpenseResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryService;
    private final AttachmentService attachmentService;
    private final ExpenseMapper expenseMapper;
    private final PaginationMapper paginationMapper;
    private final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    public ExpenseResponse getExpense(Long id) {
        var expense = expenseRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        return expenseMapper.toResponse(expense);
    }

    public PaginatedData<ExpenseResponse> getExpenses(GetExpenseFilter filter, Pageable pageable) {
        if (filter == null) {
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            filter = new GetExpenseFilter(firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    lastDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        var isValid = isFilterValid(filter);
        Page<Expense> expenses;
        if (!isValid) {
            expenses = expenseRepository.findAllByUserOrderByCreatedAtDesc(getCurrentUser(), pageable);
        } else {
            expenses = expenseRepository.findAllByUserAndCreatedAtBetweenOrderByCreatedAtDesc(getCurrentUser(),
                        filter.startDate().atStartOfDay().toInstant(ZoneOffset.UTC),
                        filter.endDate().atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC), pageable);
        }
        PaginatedData<ExpenseResponse> paginatedData = paginationMapper.toPaginatedData(expenses.map(expenseMapper::toResponse));
        return paginatedData;
    }

    public ExpenseResponse createExpense(ExpenseRequest expenseRequest) {
        logger.info("Creating expense: {}", expenseRequest.attachments());
        var expense = handleExpense(new Expense(), expenseRequest);
        @SuppressWarnings("null")
        var savedExpense = expenseRepository.save(expense);

        if (expenseRequest.attachments() != null) {
            var attachments = attachmentService.bulkCreateAttachments(savedExpense, expenseRequest.attachments());
            savedExpense.setAttachments(attachments);
            return expenseMapper.toResponse(expenseRepository.save(savedExpense));
        }
        return expenseMapper.toResponse(savedExpense);
    }

    @SuppressWarnings("null")
    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest expenseRequest) {
        var existingExpense = expenseRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));
        var expense = handleExpense(existingExpense, expenseRequest);
        var savedExpense = expenseRepository.save(expense);

        if (expenseRequest.attachments() != null) {
            var attachments = attachmentService.bulkCreateAttachments(savedExpense, expenseRequest.attachments());
            savedExpense.setAttachments(attachments);
            return expenseMapper.toResponse(expenseRepository.save(savedExpense));
        }
        return expenseMapper.toResponse(savedExpense);
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

    public ExpenseResponse bulkDeleteAttachment(Long expenseId, RemoveAttachmentRequest request) {
        var expense = expenseRepository.findByIdAndUser(expenseId, getCurrentUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));

        var attachments = expense.getAttachments().stream()
                .filter(attachment -> request.attachmentIds().contains(attachment.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        attachmentService
                .deleteAttachmentsByIds(attachments.stream().map(Attachment::getId).collect(Collectors.toList()));

        expense.getAttachments().removeAll(attachments);

        return expenseMapper.toResponse(expenseRepository.saveAndFlush(expense));
    }

    public ExpenseResponse addAttachments(Long expenseId, AddAttachmentRequest request) {
        var expense = expenseRepository.findByIdAndUser(expenseId, getCurrentUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));

        var attachments = attachmentService.bulkCreateAttachments(expense, request.attachments());
        expense.getAttachments().addAll(attachments);

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    public ExpenseResponse markExpenseAsDone(Long id) {
        var expense = expenseRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXPENSE_NOT_FOUND));

        expense.setType(ExpenseType.DONE);
        expense.setPaidDate(Instant.now());

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    private void validateExpense(Expense expense) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (expense.getType().equals(ExpenseType.RECURRING) && expense.getFrequency() == null) {
            errors.add(Map.of("frequency", "Frequency is required for RECURRING expenses"));
        }
        if ((expense.getType().equals(ExpenseType.RECURRING) || expense.getType().equals(ExpenseType.FIXED))
                && expense.getNextPaymentDate() == null) {
            errors.add(Map.of("next_payment_date", "Next Payment Date is required for RECURRING and FIXED expenses"));
        }
        if (errors.size() > 0) {
            throw new CustomValidationException(errors);
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

        if (expenseRequest.nextPaymentDate() != null
                && (expense.getType().equals(ExpenseType.RECURRING) || expense.getType().equals(ExpenseType.FIXED))) {
            expense.setNextPaymentDate(expenseRequest.nextPaymentDate().atZone(ZoneOffset.UTC).toInstant());
        }

        validateExpense(expense);

        if (expense.getType().equals(ExpenseType.DONE) && expense.getPaidDate() == null) {
            expense.setPaidDate(expenseRequest.paidDate() == null ? Instant.now()
                    : expenseRequest.paidDate().atZone(ZoneOffset.UTC).toInstant());
        }

        if (expenseRequest.categoryId() != null) {
            var category = categoryService.findByIdAndUser(expenseRequest.categoryId(), getCurrentUser())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            expense.setCategory(category);
        } else {
            expense.setCategory(null);
        }

        return expense;
    }

    private boolean isFilterValid(GetExpenseFilter filter) {
        List<Map<String, String>> errors = new ArrayList<>();
        
        if (filter.startDate() == null || filter.endDate() == null) {
            if (filter.startDate() != null){
                errors.add(Map.of("end_date", "Start date is provided but End date is not"));
            } else if (filter.endDate() != null) {
                errors.add(Map.of("start_date", "End date is provided but Start date is not"));
            } else {
                return false;
            }
        }
        if (filter.startDate() != null && !(filter.startDate() instanceof LocalDate)) {
            errors.add(Map.of("start_date", "Start date must be a valid date format YYYY-MM-DD"));
        }
        if (filter.endDate() != null && !(filter.endDate() instanceof LocalDate)) {
            errors.add(Map.of("end_date", "End date must be a valid date format YYYY-MM-DD"));
        }
        if (filter.startDate() != null && filter.endDate() != null
                && filter.startDate().isAfter(filter.endDate())) {
            errors.add(Map.of("start_date", "Start date must be before end date"));
        }
        if (errors.size() > 0) {
            throw new CustomValidationException(errors);
        }
        return true;
    }

}
