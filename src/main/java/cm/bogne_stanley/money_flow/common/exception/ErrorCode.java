package cm.bogne_stanley.money_flow.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Erreurs d'authentification
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_ACTIVATED("Account not activated", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("Email already exists", HttpStatus.BAD_REQUEST),
    // Erreurs de code d'activation
    INVALID_CODE("Invalid activation code", HttpStatus.BAD_REQUEST),
    CODE_NOT_FOUND("Activation code not found", HttpStatus.NOT_FOUND),
    CODE_ALREADY_USED("Your account is already activated.", HttpStatus.BAD_REQUEST),
    CODE_EXPIRED("Activation code expired", HttpStatus.GONE),
    
    // Erreurs de token
    INVALID_TOKEN("Invalid token", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("Token expired", HttpStatus.FORBIDDEN),
    
    // Erreurs générales
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    // Erreurs de catégorie
    CATEGORY_ALREADY_EXISTS("Category already exists", HttpStatus.BAD_REQUEST),

    // Erreurs de fichier
    FILE_TRANSFER_ERROR("File transfer error", HttpStatus.INTERNAL_SERVER_ERROR);


    @NonNull
    private final String message;
    @NonNull
    private final HttpStatus httpStatus;
}

