package cm.bogne_stanley.money_flow.common.exception;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.annotation.JsonAlias;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        logger.warn("Business error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(Map.of(
                    "error", ex.getMessage(),
                    "code", ex.getErrorCode().name()
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        logger.error("Unauthorized access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        Object target = ex.getTarget();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String message = fieldError.getDefaultMessage();
            if (String.valueOf(fieldError.getCode()).equals("typeMismatch")){
                message = "Invalid type for field " + fieldError.getField();
            }
            errors.putIfAbsent(getJsonAliasName(target, fieldError.getField()), message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomValidationException(CustomValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getErrors().forEach(error -> {
            error.forEach((key, value) -> errors.putIfAbsent(key, value));
        });
        logger.warn("Custom validation error on fields: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        logger.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthenticated"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException(NoResourceFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        logger.error("Error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

      /**
     * Extrait le(s) nom(s) JSON du champ depuis l'annotation @JsonAlias,
     * ou retourne le nom du champ Java si l'annotation n'existe pas.
     * Si l'annotation existe, retourne le premier alias, sinon le nom Java.
     */
      private String getJsonAliasName(Object target, String fieldName) {
        if (target == null) {
            return fieldName;
        }
        
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            JsonAlias jsonAlias = field.getAnnotation(JsonAlias.class);
            if (jsonAlias != null && jsonAlias.value().length > 0) {
                return jsonAlias.value()[0];
            }
        } catch (NoSuchFieldException | SecurityException e) {
            // Si le champ n'existe pas ou n'est pas accessible, retourner le nom original
        }
        
        return fieldName;
    }
    
}
