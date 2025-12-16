package cm.bogne_stanley.money_flow.common.custom_validator.validator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDate, Object> {
    private final Logger logger = LoggerFactory.getLogger(DateValidator.class);

    private boolean isDateTime;
    private boolean optional;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return optional;
        }
        if (value instanceof String) {
            try {
                if (isDateTime) {
                    LocalDateTime.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } else {
                    LocalDate.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            } catch (DateTimeException e) {
                logger.warn("Invalid date format: {}", value);
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
        this.isDateTime = constraintAnnotation.isDateTime();
    }
}

