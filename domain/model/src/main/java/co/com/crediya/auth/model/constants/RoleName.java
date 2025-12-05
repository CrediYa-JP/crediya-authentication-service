package co.com.crediya.auth.model.constants;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleName {
    CUSTOMER(1L),
    ADVISOR(2L),
    ADMINISTRATOR(3L);

    private final Long id;

    RoleName(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    public static RoleName fromId(Long id) {
        return Arrays.stream(values())
                .filter(role -> role.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));
    }

    public static RoleName fromAuthority(String authority) {
        String roleName = authority.startsWith("ROLE_")
                ? authority.substring(5)
                : authority;

        return Arrays.stream(values())
                .filter(role -> role.name().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid authority: " + authority));
    }
}