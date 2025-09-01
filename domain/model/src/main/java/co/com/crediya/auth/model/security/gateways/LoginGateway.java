package co.com.crediya.auth.model.security.gateways;

import reactor.core.publisher.Mono;

public interface LoginGateway {

    Mono<String> authenticateAndGenerateToken(String email, String password);
}