package cm.bogne_stanley.money_flow.common.custom_validator.validator;


import java.util.List;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidEnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValidEnumValue constraintAnnotation) {
        this.acceptedValues = java.util.Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return acceptedValues.contains(value);
    }
    
}

