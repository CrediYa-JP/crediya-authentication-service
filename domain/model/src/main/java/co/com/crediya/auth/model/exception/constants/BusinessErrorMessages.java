package co.com.crediya.auth.model.exception.constants;

public final class BusinessErrorMessages {

    public static final String USER_ALREADY_EXISTS_MSG = "Duplicate identity document detected in authentication system";
    public static final String USER_NOT_FOUND_MSG = "User not found in authentication system";
    public static final String INVALID_CREDENTIALS_MSG = "Invalid credentials provided";


    private BusinessErrorMessages() {
    }
}