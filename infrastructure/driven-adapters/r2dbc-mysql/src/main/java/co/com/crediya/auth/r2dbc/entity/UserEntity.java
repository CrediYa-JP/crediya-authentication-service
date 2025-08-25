package co.com.crediya.auth.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column("user_id")
    private Long userId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("email")
    private String email;

    @Column("identity_document")
    private String identityDocument;

    @Column("phone")
    private String phone;

    @Column("role_id")
    private Long roleId;

    @Column("base_salary")
    private BigDecimal baseSalary;

    @Column("password")
    private String password;

    @Column("creation_date")
    private LocalDateTime creationDate;
}