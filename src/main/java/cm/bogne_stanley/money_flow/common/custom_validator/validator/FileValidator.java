package cm.bogne_stanley.money_flow.common.custom_validator.validator;

import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private boolean optional;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())) {
            return true;
        }
        if (value == null || value.isEmpty()) {
            return false;
        }
        return value.getContentType() != null;
    }
    
}

