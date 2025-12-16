package cm.bogne_stanley.money_flow.presentation.dto.request.stats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidDate;
import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidEnumValue;
import cm.bogne_stanley.money_flow.data.entity.ExpenseType;

public record StatsFilter(
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ValidDate(message = "Start date must be a valid date format YYYY-MM-DD", optional = true)
    @JsonProperty("start_date")
    String start_date,
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ValidDate(message = "End date must be a valid date format YYYY-MM-DD", optional = true)
    @JsonProperty("end_date")
    String end_date,
    @ValidEnumValue(enumClass = ExpenseType.class, message = "Type must be one of: FIXED, RECURRING, DONE")
    String type,
    @JsonProperty("category_id")
    Long category_id
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

