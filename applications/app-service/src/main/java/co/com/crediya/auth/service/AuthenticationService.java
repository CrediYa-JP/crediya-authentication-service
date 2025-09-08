package co.com.crediya.auth.service;

import co.com.crediya.auth.model.exception.security.InvalidCredentialsException;
import co.com.crediya.auth.model.security.gateways.LoginGateway;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements LoginGateway {

    private final UserRepository userRepository;
    private final PasswordService passwordEncoder;
    private final RSAPrivateKey privateKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Override
    public Mono<String> authenticateAndGenerateToken(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .map(this::generateToken);
    }

    private String generateToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("roleId", user.getRoleId())
                .claim("firstName", user.getFirstName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtExpiration, ChronoUnit.MILLIS)))
                .signWith(privateKey)
                .compact();
    }
}