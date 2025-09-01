package co.com.crediya.auth.service;

import co.com.crediya.auth.model.security.gateways.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceIml implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}