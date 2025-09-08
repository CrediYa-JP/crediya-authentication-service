package co.com.crediya.auth.usecase;

import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.constants.RoleConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
                .password("plainPassword123")  // Plain password antes del hash
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .creationDate(LocalDateTime.now())
                .build();
    }

    public static User buildValidUserWithHashedPassword() {
        return buildValidUser().toBuilder()
                .password("$2a$10$hashedPassword123") // Password ya hasheado
                .build();
    }

    public static User buildSavedUser() {
        return buildValidUserWithHashedPassword().toBuilder()
                .userId(1L)
                .creationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();
    }

    public static User buildSecondUser() {
        return User.builder()
                .firstName("Maria")
                .lastName("Rodriguez")
                .email("maria@test.com")
                .identityDocument("87654321")
                .phone("3009876543")
                .baseSalary(new BigDecimal("3000000"))
                .password("plainPassword456")
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .creationDate(LocalDateTime.now())
                .build();
    }

    public static User buildSecondSavedUser() {
        return buildSecondUser().toBuilder()
                .userId(2L)
                .password("$2a$10$hashedPassword456")
                .creationDate(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
                .build();
    }

    public static User buildThirdSavedUser() {
        return User.builder()
                .userId(3L)
                .firstName("Carlos")
                .lastName("Lopez")
                .email("carlos@test.com")
                .identityDocument("11223344")
                .phone("3005544332")
                .baseSalary(new BigDecimal("4500000"))
                .password("$2a$10$hashedPassword789")
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .build();
    }

    public static List<String> buildValidIdentityDocumentsList() {
        return Arrays.asList("12345678", "87654321", "11223344");
    }

    public static List<User> buildSavedUsersList() {
        return Arrays.asList(
                buildSavedUser(),
                buildSecondSavedUser(),
                buildThirdSavedUser()
        );
    }

    public static User buildUserWithNullableFields() {
        return User.builder()
                .firstName("Ana")
                .lastName("Silva")
                .email("ana@test.com")
                .identityDocument("99887766")
                .phone(null) // Campo nullable
                .baseSalary(new BigDecimal("2500000"))
                .password("plainPassword999")
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .build();
    }

    public static User buildUserWithEdgeCaseData() {
        return User.builder()
                .firstName("José María")  // Nombres compuestos
                .lastName("Rodríguez-Pérez") // Apellidos con guión
                .email("jose.maria@domain-test.com") // Email complejo
                .identityDocument("1234567890") // Documento largo
                .phone("") // String vacío
                .baseSalary(BigDecimal.ZERO) // Salario mínimo
                .password("a") // Password mínimo
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .build();
    }
}