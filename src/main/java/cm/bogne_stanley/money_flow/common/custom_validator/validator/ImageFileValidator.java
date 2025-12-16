package cm.bogne_stanley.money_flow.common.custom_validator.validator;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidImageFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, Object> {
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
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return optional;
        }

        if (!(value instanceof MultipartFile) && !(value instanceof List)) {
            return false;
        }

        if (value instanceof List) {
            List<?> fileList = (List<?>) value;
            for (Object obj : fileList) {
                if (!(obj instanceof MultipartFile)) {
                    return false;
                }
                MultipartFile file = (MultipartFile) obj;
                if (file == null || file.isEmpty() || !isImageFile(file)) {
                    return false;
                }
            }
            return true;
        }

        MultipartFile file = (MultipartFile) value;

        if (file == null || file.isEmpty() || !isImageFile(file)) {
            return false;
        }
        return true;
    }
}
