package com.csvmanager.domain.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FiscalCodeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFiscalCode {
  String message() default "Invalid fiscal code";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
