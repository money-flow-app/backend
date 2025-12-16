package cm.bogne_stanley.money_flow.common.mappers;

import org.springframework.stereotype.Component;

import cm.bogne_stanley.money_flow.data.entity.Expense;
import cm.bogne_stanley.money_flow.presentation.dto.response.expense.ExpenseResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpenseMapper {
    private final AttachmentMapper attachmentMapper;
    private final CategoryMapper categoryMapper;

    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getTitle(),
            expense.getAmount(),
            expense.getType(),
            expense.getFrequency(),
            expense.getNextPaymentDate(),
            expense.getPaidDate(),
            expense.getDescription(),
            expense.getNote(),
            expense.getAttachments() != null ? expense.getAttachments().stream().map(attachmentMapper::toResponse).toList() : null,
            expense.getCategory() != null ? categoryMapper.toResponse(expense.getCategory()) : null,
            expense.getCreatedAt(),
            expense.getUpdatedAt()
        );
    }
}
