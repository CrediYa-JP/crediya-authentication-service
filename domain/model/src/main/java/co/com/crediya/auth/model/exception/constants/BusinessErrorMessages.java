package co.com.crediya.auth.model.exception.constants;

public final class BusinessErrorMessages {

    public static final String USER_ALREADY_EXISTS_MSG = "User with this email already exists";
    public static final String INVALID_EMAIL_FORMAT_MSG = "Email format is invalid";
    public static final String INVALID_SALARY_RANGE_MSG = "Salary must be between 0 and 150,000,000";
    public static final String REQUIRED_FIELD_MISSING_MSG = "Required field is missing";

    private BusinessErrorMessages() {
    }
}