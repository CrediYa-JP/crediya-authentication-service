package co.com.crediya.auth.api.mapper;

import co.com.crediya.auth.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.auth.model.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;


public class HttpStatusMapper {
    private HttpStatusMapper() {
    }

    // HttpStatusMapper.java
    public static HttpStatus mapToHttpStatus(ErrorCode errorCode) {
        return switch(errorCode.getCode()) {
            case BusinessErrorCodes.USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case BusinessErrorCodes.INVALID_EMAIL_FORMAT-> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}