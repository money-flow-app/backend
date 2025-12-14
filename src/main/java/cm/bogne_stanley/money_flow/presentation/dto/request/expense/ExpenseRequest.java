package cm.bogne_stanley.money_flow.presentation.dto.request.expense;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonAlias;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidDate;
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
    @ValidDate(message = "Next payment date must be a valid date format YYYY-MM-DD HH:mm:ss", isDateTime = true)
    String next_payment_date,
    @JsonAlias("paid_date")
    @ValidDate(message = "Paid date must be a valid date format YYYY-MM-DD HH:mm:ss", isDateTime = true)
    String paid_date,
    String description,
    String note,
    @JsonAlias("category_id")
    Long categoryId,
    @ValidFile(message = "attachments must be a list of file", optional = true)
    List<MultipartFile> attachments
) {
    public LocalDateTime nextPaymentDate() {
        return parseDate(next_payment_date);
    }
    
    public LocalDateTime paidDate() {
        return parseDate(paid_date);
    }
    
    private LocalDateTime parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
