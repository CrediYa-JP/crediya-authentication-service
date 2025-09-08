package co.com.crediya.auth.model.user.gateways;

import co.com.crediya.auth.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);

    Mono<User> findByEmail(String email);
    Mono<User> findByIdentityDocument(String identityDocument);
    Mono<Boolean> existsByEmail(String email);

    Mono<List<User>> findByIdentityDocuments(List<String> identityDocuments);

}
