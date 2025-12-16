package cm.bogne_stanley.money_flow.common.custom_validator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cm.bogne_stanley.money_flow.common.custom_validator.validator.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValueValidator.class})
public @interface ValidEnumValue {

    Class<? extends Enum<?>> enumClass();

    String message() default "Invalid Value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
