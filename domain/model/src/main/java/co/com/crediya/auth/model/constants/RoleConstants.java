package co.com.crediya.auth.model.constants;

public final class RoleConstants {

    public static final Long CUSTOMER_ROLE_ID = 1L;
    public static final Long ADVISOR_ROLE_ID = 2L;
    public static final Long ADMINISTRATOR_ROLE_ID = 3L;

    private RoleConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}