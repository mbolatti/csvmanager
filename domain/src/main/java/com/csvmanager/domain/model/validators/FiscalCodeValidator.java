package com.csvmanager.domain.model.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class FiscalCodeValidator implements ConstraintValidator<ValidFiscalCode, String> {

  private static final Pattern FISCAL_CODE_PATTERN = Pattern.compile(
      "^[A-Z]{6}\\d{2}[A-Z]{1}\\d{2}[A-Z]\\d{3}[A-Z]$");

  @Override
  public void initialize(ValidFiscalCode constraintAnnotation) {
  }

  @Override
  public boolean isValid(String fiscalCode, ConstraintValidatorContext context) {
    if (fiscalCode == null) {
      return false;
    }

    if (fiscalCode.length() != 16) {
      return false;
    }

    if (!FISCAL_CODE_PATTERN.matcher(fiscalCode).matches()) {
      return false;
    }

    return true;
  }
}
