package co.com.crediya.auth.model.user;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;
    private String phone;
    private Long roleId;
    private BigDecimal baseSalary;
    private String password;
    private LocalDateTime creationDate;
}
