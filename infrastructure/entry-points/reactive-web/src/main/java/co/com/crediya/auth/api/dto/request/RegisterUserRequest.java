package co.com.crediya.auth.api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Identity document is required")
    private String identityDocument;

    private String phone;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0", message = "Salary must be greater than or equal to 0")
    @DecimalMax(value = "150000000", message = "Salary must be less than or equal to 150,000,000")
    private BigDecimal baseSalary;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}