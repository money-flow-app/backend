package cm.bogne_stanley.money_flow.presentation.dto.request.expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonFormat;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidDate;

public record GetExpenseFilter(
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ValidDate(message = "Start date must be a valid date format YYYY-MM-DD", optional = true)
    String start_date,
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ValidDate(message = "End date must be a valid date format YYYY-MM-DD", optional = true)
    String end_date
) {

    public LocalDate startDate() {
        return parseDate(start_date);
    }
    
    public LocalDate endDate() {
        return parseDate(end_date);
    }
    
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            // Cette erreur sera gérée par le validator
            return null;
        }
    }
    
}
