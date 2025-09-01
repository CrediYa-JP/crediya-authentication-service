package co.com.crediya.auth.model.exception.security;

import co.com.crediya.auth.model.exception.BusinessException;
import co.com.crediya.auth.model.exception.errorcode.AuthenticationErrorCode;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super(AuthenticationErrorCode.INVALID_CREDENTIALS);
    }
}