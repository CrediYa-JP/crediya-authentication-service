package co.com.crediya.auth.config.security;

import co.com.crediya.auth.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RSAPrivateKey privateKey;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpiration;

    public String generateToken(User user) {
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