package com.evgeniyfedorchenko.animalshelter.admin.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsFieldValidator.class)
public @interface IsFieldOf {

    String message() default "The field contains invalid characters";

    Class<?> value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
