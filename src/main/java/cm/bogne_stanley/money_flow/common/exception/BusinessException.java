package cm.bogne_stanley.money_flow.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    @NonNull
    private final ErrorCode errorCode;
    @NonNull
    private final HttpStatus httpStatus;

    public BusinessException(@NonNull ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public BusinessException(@NonNull ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }
}

