package co.com.crediya.auth.model.security;

import reactor.core.publisher.Mono;

public interface LoginGateway {

    Mono<String> authenticateAndGenerateToken(String email, String password);
}