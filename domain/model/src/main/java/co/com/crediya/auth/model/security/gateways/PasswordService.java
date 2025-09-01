package co.com.crediya.auth.model.security.gateways;

public interface PasswordService {

    String encode(String plainPassword);

    boolean matches(String plainPassword, String hashedPassword);
}