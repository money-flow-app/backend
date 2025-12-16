package cm.bogne_stanley.money_flow.common.custom_validator.validator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidFile, Object> {
    private final Logger logger = LoggerFactory.getLogger(FileValidator.class);

    private boolean optional;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return optional;
        }
        if (!(value instanceof MultipartFile) && !(value instanceof List)) {
            logger.warn("Invalid file type or list: {}", value.getClass());
            logger.warn("Value: {}", value instanceof List );
            return false;
        }

        if (value instanceof List) {
            List<?> fileList = (List<?>) value;
            if (optional && (fileList == null || fileList.isEmpty())) {
                return true;
            }
            for (Object obj : fileList) {
                if (!(obj instanceof MultipartFile)) {
                    logger.warn("Invalid file type: {}", obj.getClass());
                    return false;
                }
                MultipartFile file = (MultipartFile) obj;
                if (file == null || file.isEmpty()) {
                    return false;
                }
                if (file.getContentType() == null) {
                    return false;
                }
            }
            return true;
        }

        MultipartFile file = (MultipartFile) value;

        if (file == null || file.isEmpty()) {
            return false;
        }
        return file.getContentType() != null;
    }
    
}

