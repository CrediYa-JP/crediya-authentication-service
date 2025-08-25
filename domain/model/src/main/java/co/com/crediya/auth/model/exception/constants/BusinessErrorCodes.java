package co.com.crediya.auth.model.exception.constants;

public final class BusinessErrorCodes {

    // Authentication codes
    public static final String USER_ALREADY_EXISTS = "AUTH001";
    public static final String INVALID_EMAIL_FORMAT = "AUTH002";
    public static final String INVALID_SALARY_RANGE = "AUTH003";
    public static final String REQUIRED_FIELD_MISSING = "AUTH004";

    private BusinessErrorCodes() {
    }
}