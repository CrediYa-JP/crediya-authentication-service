package co.com.crediya.auth.security.adapters;

import co.com.crediya.auth.model.exception.security.InvalidCredentialsException;
import co.com.crediya.auth.model.security.gateways.LoginGateway;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import co.com.crediya.auth.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceAdapter  implements LoginGateway {

    private final UserRepository userRepository;
    private final PasswordService passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Override
    public Mono<String> authenticateAndGenerateToken(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .map(jwtTokenService::generateToken);
    }

}