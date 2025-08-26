package co.com.crediya.auth.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterUserRequest {

    @Schema(description = "User first name", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User last name", example = "Perez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "User email address", example = "juan.perez@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Identity document number", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Identity document is required")
    private String identityDocument;

    @Schema(description = "Phone number", example = "3001234567")
    private String phone;

    @Schema(description = "Monthly salary", example = "5000000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0", message = "Salary must be greater than or equal to 0")
    @DecimalMax(value = "150000000", message = "Salary must be less than or equal to 150,000,000")
    private BigDecimal baseSalary;

    @Schema(description = "User password", example = "mySecurePass123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}