package co.com.crediya.auth.usecase.user;

import co.com.crediya.auth.model.exception.user.UserNotFoundException;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class RetrieveUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> executeByIdentityDocument(String identityDocument) {
        return userRepository.findByIdentityDocument(identityDocument)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    public Mono<List<User>> executeByIdentityDocuments(List<String> identityDocuments) {
        return userRepository.findByIdentityDocuments(identityDocuments);
    }
}