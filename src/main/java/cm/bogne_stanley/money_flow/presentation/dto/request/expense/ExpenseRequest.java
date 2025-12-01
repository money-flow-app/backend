package cm.bogne_stanley.money_flow.presentation.dto.request.expense;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonAlias;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidEnumValue;
import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidFile;
import cm.bogne_stanley.money_flow.data.entity.ExpenseFrequency;
import cm.bogne_stanley.money_flow.data.entity.ExpenseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpenseRequest(
    @NotBlank(message = "Title is required")
    String title,
    @NotNull(message = "Amount is required")
    Double amount,
    @NotBlank(message = "Type is required")
    @ValidEnumValue(enumClass = ExpenseType.class, message = "Type must be DONE, FIXED or RECURRING")
    String type,
    @ValidEnumValue(enumClass = ExpenseFrequency.class, message = "Frequency must be DAILY, WEEKLY, MONTHLY or YEARLY")
    String frequency,
    @JsonAlias("next_payment_date")
    LocalDateTime nextPaymentDate,
    @JsonAlias("paid_date")
    LocalDateTime paidDate,
    String description,
    String note,
    @JsonAlias("category_id")
    Long categoryId,
    @ValidFile(message = "attachments must be a list of file", optional = true)
    List<MultipartFile> attachments
) {
    
}
