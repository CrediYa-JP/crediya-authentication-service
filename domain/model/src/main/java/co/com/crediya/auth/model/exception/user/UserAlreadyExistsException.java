package co.com.crediya.auth.model.exception.user;

import co.com.crediya.auth.model.exception.BusinessException;
import co.com.crediya.auth.model.exception.errorcode.AuthenticationErrorCode;

public class UserAlreadyExistsException extends BusinessException {

    public UserAlreadyExistsException() {
        super(AuthenticationErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(String email) {
        super(AuthenticationErrorCode.USER_ALREADY_EXISTS,
                String.format("User with email '%s' already exists", email));
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(AuthenticationErrorCode.USER_ALREADY_EXISTS, cause);
    }
}