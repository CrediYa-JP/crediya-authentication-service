package co.com.crediya.auth.model.exception.user;

import co.com.crediya.auth.model.exception.BusinessException;
import co.com.crediya.auth.model.exception.errorcode.AuthenticationErrorCode;

import java.math.BigDecimal;

public class InvalidSalaryException extends BusinessException {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("150000000");

    public InvalidSalaryException() {
        super(AuthenticationErrorCode.INVALID_SALARY_RANGE);
    }

    public InvalidSalaryException(BigDecimal salary) {
        super(AuthenticationErrorCode.INVALID_SALARY_RANGE,
                String.format("Salary '%s' is out of valid range [%s - %s]",
                        salary, MIN_SALARY, MAX_SALARY));
    }

    public InvalidSalaryException(Throwable cause) {
        super(AuthenticationErrorCode.INVALID_SALARY_RANGE, cause);
    }
}