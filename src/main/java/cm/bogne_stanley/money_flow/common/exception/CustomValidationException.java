package cm.bogne_stanley.money_flow.common.exception;

public class CustomValidationException extends RuntimeException {
    private String field;
    public CustomValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
