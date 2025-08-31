package co.com.crediya.auth.model.exception.user;

import co.com.crediya.auth.model.exception.BusinessException;
import co.com.crediya.auth.model.exception.errorcode.AuthenticationErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(AuthenticationErrorCode.USER_NOT_FOUND);
    }

}
