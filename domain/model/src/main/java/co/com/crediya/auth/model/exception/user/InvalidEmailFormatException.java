package co.com.crediya.auth.model.exception.user;

import co.com.crediya.auth.model.exception.BusinessException;
import co.com.crediya.auth.model.exception.errorcode.AuthenticationErrorCode;

public class InvalidEmailFormatException extends BusinessException {

    public InvalidEmailFormatException() {
        super(AuthenticationErrorCode.INVALID_EMAIL_FORMAT);
    }

    public InvalidEmailFormatException(String email) {
        super(AuthenticationErrorCode.INVALID_EMAIL_FORMAT,
                String.format("Email format '%s' is invalid", email));
    }

    public InvalidEmailFormatException(Throwable cause) {
        super(AuthenticationErrorCode.INVALID_EMAIL_FORMAT, cause);
    }
}