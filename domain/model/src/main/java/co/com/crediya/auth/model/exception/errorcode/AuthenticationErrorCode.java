package co.com.crediya.auth.model.exception.errorcode;

import co.com.crediya.auth.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.auth.model.exception.constants.BusinessErrorMessages;

public enum AuthenticationErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(BusinessErrorCodes.USER_ALREADY_EXISTS,
            BusinessErrorMessages.USER_ALREADY_EXISTS_MSG),
    INVALID_EMAIL_FORMAT(BusinessErrorCodes.INVALID_EMAIL_FORMAT,
            BusinessErrorMessages.INVALID_EMAIL_FORMAT_MSG),
    INVALID_SALARY_RANGE(BusinessErrorCodes.INVALID_SALARY_RANGE,
            BusinessErrorMessages.INVALID_SALARY_RANGE_MSG),
    REQUIRED_FIELD_MISSING(BusinessErrorCodes.REQUIRED_FIELD_MISSING,
            BusinessErrorMessages.REQUIRED_FIELD_MISSING_MSG);

    private final String code;
    private final String defaultMessage;

    AuthenticationErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}