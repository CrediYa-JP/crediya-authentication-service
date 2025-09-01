package co.com.crediya.auth.usecase;

import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.constants.RoleConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestDataBuilder {

    private TestDataBuilder() {}

    public static User buildValidUser() {
        return User.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .identityDocument("12345678")
                .phone("3001234567")
                .baseSalary(new BigDecimal("5000000"))
                .password("hashedPassword123")
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .creationDate(LocalDateTime.now())
                .build();
    }


    public static User buildSavedUser() {
        return buildValidUser().toBuilder()
                .userId(1L)
                .build();
    }
}