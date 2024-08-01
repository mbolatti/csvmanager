import com.csvmanager.domain.model.PersonalData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.Set;

public class PersonalDataTest {

  public static final String IMPORT_ID = "321";
  public static final String NAME = "John";
  public static final String LAST_NAME = "Doe";
  public static final String INVALID_DATE = "1990-01-01";
  public static final String VALID_DATE = "01/01/1990";
  public static final String CITY = "New York";
  public static final long LINE_ID = 1L;
  public static final String STRING_EXCEED_50CHARS = "jksadjsalkdjskdjskdjksdjksajdkadjklajdkalsjdkaljdlk";
  String VALID_FC = "FRSMNS78S21F600Z";

  @Test
  void testValidPersonalData() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, LAST_NAME, VALID_DATE, CITY, VALID_FC);
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertTrue(violations.isEmpty(), "Personal data should be valid");
  }

  @Test
  void testInvalidBlankName() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, "", LAST_NAME, VALID_DATE, CITY, VALID_FC);
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Name cannot be blank");
  }

  @Test
  void testInvalidExceededSizeName() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, STRING_EXCEED_50CHARS, LAST_NAME, VALID_DATE, CITY, VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Name cannot be longer than 50 characters");
  }

  @Test
  void testInvalidBlankLastName() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, "", VALID_DATE, CITY, VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Last name cannot be blank");
  }

  @Test
  void testInvalidExceededSizeLastName() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, STRING_EXCEED_50CHARS, VALID_DATE, CITY, VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Last name cannot be longer than 50 characters");
  }

  @Test
  void testInvalidBlankBirthDate() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, LAST_NAME,"", CITY, VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Birth date cannot be blank");
  }

  @Test
  void testInvalidFormatBirthDate() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME,LAST_NAME, INVALID_DATE, CITY, VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Birth date must be in the format DD/MM/YYYY");
  }

  @Test
  void testInvalidBlankCity() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, LAST_NAME,VALID_DATE, "", VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "City cannot be blank");
  }

  @Test
  void testInvalidExceededSizeCity() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME,LAST_NAME, INVALID_DATE, STRING_EXCEED_50CHARS+STRING_EXCEED_50CHARS, VALID_FC);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "City cannot be longer than 100 characters");
  }

  @Test
  void testInvalidBlankFiscalCode() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, LAST_NAME,VALID_DATE, CITY, "");

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Fiscal code cannot be blank");
  }

  @Test
  void testInvalidFormatFiscalCode() {
    PersonalData personalData = new PersonalData(LINE_ID, IMPORT_ID, NAME, LAST_NAME,VALID_DATE, CITY, "ABCD1235ADD600Z");

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<PersonalData>> violations = validator.validate(personalData);

    Assertions.assertFalse(violations.isEmpty(), "Invalid fiscal code");
  }
}



