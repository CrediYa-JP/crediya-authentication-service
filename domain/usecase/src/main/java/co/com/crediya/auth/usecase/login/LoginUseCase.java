package co.com.crediya.auth.usecase.login;

import co.com.crediya.auth.model.security.gateways.LoginGateway;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final LoginGateway loginGateway;

    public Mono<String> execute(String email, String password) {
        return loginGateway.authenticateAndGenerateToken(email, password);
    }
}