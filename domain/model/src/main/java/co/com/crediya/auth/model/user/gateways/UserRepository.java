package co.com.crediya.auth.model.user.gateways;

import co.com.crediya.auth.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
}
