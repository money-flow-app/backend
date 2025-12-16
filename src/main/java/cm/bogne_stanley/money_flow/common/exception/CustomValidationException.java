package cm.bogne_stanley.money_flow.common.exception;

import java.util.List;
import java.util.Map;

public class CustomValidationException extends RuntimeException {
    List<Map<String, String>> errors;
    public CustomValidationException(List<Map<String, String>> errors) {
        super("Validation error");
        this.errors = errors;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }
}
