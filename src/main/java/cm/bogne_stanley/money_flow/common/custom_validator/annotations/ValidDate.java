package cm.bogne_stanley.money_flow.common.custom_validator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cm.bogne_stanley.money_flow.common.custom_validator.validator.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateValidator.class})
public @interface ValidDate {
    String message() default "Invalid date format YYYY-MM-DD";

    boolean isDateTime() default false;

    boolean optional() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
