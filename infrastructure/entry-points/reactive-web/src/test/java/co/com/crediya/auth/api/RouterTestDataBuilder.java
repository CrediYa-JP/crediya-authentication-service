package co.com.crediya.auth.api;

import co.com.crediya.auth.api.dto.request.LoginRequest;
import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.LoginResponse;
import co.com.crediya.auth.api.dto.response.UserResponse;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.constants.RoleConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public final class RouterTestDataBuilder {

    private RouterTestDataBuilder() {}

    // ================== REQUEST DTOs ==================

    public static RegisterUserRequest buildValidRegisterUserRequest() {
        return new RegisterUserRequest(
                "Juan",
                "Perez",
                "juan@test.com",
                "12345678",
                "3001234567",
                new BigDecimal("5000000"),
                "password123"
        );
    }

    public static RegisterUserRequest buildRegisterUserRequestWithNullableFields() {
        return new RegisterUserRequest(
                "Ana",
                "Silva",
                "ana@test.com",
                "99887766",
                null, // phone nullable
                new BigDecimal("2500000"),
                "password456"
        );
    }

    public static RegisterUserRequest buildRegisterUserRequestWithEdgeCases() {
        return new RegisterUserRequest(
                "José María",
                "Rodríguez-Pérez",
                "jose.maria@domain-test.com",
                "1234567890",
                "",  // empty phone
                BigDecimal.ZERO,
                "a"  // minimum password
        );
    }

    public static RegisterUserRequest buildInvalidRegisterUserRequest() {
        return new RegisterUserRequest(
                "", // invalid: empty first name
                "",  // invalid: empty last name
                "invalid-email", // invalid: bad email format
                "", // invalid: empty document
                "3001234567",
                new BigDecimal("-1000"), // invalid: negative salary
                "" // invalid: empty password
        );
    }

    public static LoginRequest buildValidLoginRequest() {
        return new LoginRequest("juan@test.com", "password123");
    }

    public static LoginRequest buildInvalidLoginRequest() {
        return new LoginRequest("invalid-email", "");
    }

    public static LoginRequest buildLoginRequestNonexistentUser() {
        return new LoginRequest("nonexistent@test.com", "password123");
    }

    public static String[] buildValidIdentityDocumentsArray() {
        return new String[]{"12345678", "87654321", "11223344"};
    }

    public static String[] buildEmptyIdentityDocumentsArray() {
        return new String[]{};
    }

    public static String[] buildSingleIdentityDocumentArray() {
        return new String[]{"12345678"};
    }

    // ================== RESPONSE DTOs ==================

    public static UserResponse buildValidUserResponse() {
        return UserResponse.builder()
                .userId(1L)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .identityDocument("12345678")
                .phone("3001234567")
                .baseSalary(new BigDecimal("5000000"))
                .creationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();
    }

    public static UserResponse buildSecondUserResponse() {
        return UserResponse.builder()
                .userId(2L)
                .firstName("Maria")
                .lastName("Rodriguez")
                .email("maria@test.com")
                .identityDocument("87654321")
                .phone("3009876543")
                .baseSalary(new BigDecimal("3000000"))
                .creationDate(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
                .build();
    }

    public static UserResponse buildThirdUserResponse() {
        return UserResponse.builder()
                .userId(3L)
                .firstName("Carlos")
                .lastName("Lopez")
                .email("carlos@test.com")
                .identityDocument("11223344")
                .phone("3005544332")
                .baseSalary(new BigDecimal("4500000"))
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .build();
    }

    public static List<UserResponse> buildUserResponsesList() {
        return Arrays.asList(
                buildValidUserResponse(),
                buildSecondUserResponse(),
                buildThirdUserResponse()
        );
    }

    public static LoginResponse buildValidLoginResponse() {
        return LoginResponse.builder()
                .token("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqdWFuQHRlc3QuY29tIiwidXNlcklkIjoxLCJyb2xlSWQiOjEsImZpcnN0TmFtZSI6Ikp1YW4iLCJpYXQiOjE2NDAwMDAwMDAsImV4cCI6MTY0MDA4NjQwMH0.signature")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();
    }

    // ================== DOMAIN MODELS (for mocking use cases) ==================

    public static User buildValidDomainUser() {
        return User.builder()
                .userId(1L)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .identityDocument("12345678")
                .phone("3001234567")
                .roleId(RoleConstants.CUSTOMER_ROLE_ID)
                .baseSalary(new BigDecimal("5000000"))
                .password("$2a$10$hashedPassword123")
                .creationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();
    }

    public static User buildSecondDomainUser() {
        return User.builder()
                .userId(2L)
                .firstName("Maria")
                .lastName("Rodriguez")
                .email("maria@test.com")
                .identityDocument("87654321")
                .phone("3009876543")
                .roleId(RoleConstants.CUSTOMER_ROLE_ID)
                .baseSalary(new BigDecimal("3000000"))
                .password("$2a$10$hashedPassword456")
                .creationDate(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
                .build();
    }

    public static User buildThirdDomainUser() {
        return User.builder()
                .userId(3L)
                .firstName("Carlos")
                .lastName("Lopez")
                .email("carlos@test.com")
                .identityDocument("11223344")
                .phone("3005544332")
                .roleId(RoleConstants.CUSTOMER_ROLE_ID)
                .baseSalary(new BigDecimal("4500000"))
                .password("$2a$10$hashedPassword789")
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .build();
    }

    public static List<User> buildDomainUsersList() {
        return Arrays.asList(
                buildValidDomainUser(),
                buildSecondDomainUser(),
                buildThirdDomainUser()
        );
    }

    public static User buildDomainUserWithNullableFields() {
        return User.builder()
                .userId(4L)
                .firstName("Ana")
                .lastName("Silva")
                .email("ana@test.com")
                .identityDocument("99887766")
                .phone(null) // nullable field
                .roleId(RoleConstants.CUSTOMER_ROLE_ID)
                .baseSalary(new BigDecimal("2500000"))
                .password("$2a$10$hashedPassword999")
                .creationDate(LocalDateTime.of(2024, 1, 18, 11, 20, 0))
                .build();
    }
}