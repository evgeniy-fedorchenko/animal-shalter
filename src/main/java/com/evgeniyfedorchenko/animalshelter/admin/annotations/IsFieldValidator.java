package com.evgeniyfedorchenko.animalshelter.admin.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsFieldValidator implements ConstraintValidator<IsFieldOf, String> {

    private Class<?> value;

    @Override
    public void initialize(IsFieldOf constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String validatedValue, ConstraintValidatorContext context) {

        if (validatedValue == null) {
            throw new IllegalArgumentException("Validated parameter or value is null");
        }
        try {
           /* Если получилось достать поле и ReflectiveOperationException не был сгенерирован,
              значит такое поле есть в классе -> возвращаем true */
            value.getDeclaredField(validatedValue);
            return true;

        } catch (ReflectiveOperationException _) {
            return false;
        }
    }
}
