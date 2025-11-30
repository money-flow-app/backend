package cm.bogne_stanley.money_flow.common.custom_validator.validator;

import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidImageFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {
    private boolean optional;

    @Override
    public void initialize(ValidImageFile constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @SuppressWarnings("null")
    private boolean isImageFile(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().startsWith("image/");
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())) {
            return true;
        }
        if (value == null || value.isEmpty() || !isImageFile(value)) {
            return false;
        }
        return true;
    }
}
