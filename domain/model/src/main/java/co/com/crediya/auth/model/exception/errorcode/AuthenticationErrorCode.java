package co.com.crediya.auth.model.exception.errorcode;

import co.com.crediya.auth.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.auth.model.exception.constants.BusinessErrorMessages;

public enum AuthenticationErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(BusinessErrorCodes.USER_ALREADY_EXISTS,
            BusinessErrorMessages.USER_ALREADY_EXISTS_MSG),
    USER_NOT_FOUND(BusinessErrorCodes.USER_NOT_FOUND, BusinessErrorMessages.USER_NOT_FOUND_MSG),
    INVALID_CREDENTIALS(BusinessErrorCodes.INVALID_CREDENTIALS,
            BusinessErrorMessages.INVALID_CREDENTIALS_MSG);

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