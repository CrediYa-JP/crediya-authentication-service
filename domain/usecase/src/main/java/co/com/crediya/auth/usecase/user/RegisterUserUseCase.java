package co.com.crediya.auth.usecase.user;

import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public Mono<User> execute(User user) {
        return Mono.fromCallable(() -> passwordService.encode(user.getPassword()))
                .map(hashedPassword -> user.toBuilder()
                        .password(hashedPassword)
                        .build())
                .flatMap(this::validateUniqueness)
                .flatMap(userRepository::save);
    }

    private Mono<User> validateUniqueness(User user) {
        return validateEmailUnique(user.getEmail())
                .then(validateIdentityDocumentUnique(user.getIdentityDocument()))
                .thenReturn(user);
    }

    private Mono<Void> validateEmailUnique(String email) {
        return userRepository.existsByEmail(email)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new UserAlreadyExistsException(email)))
                .then();
    }

    private Mono<Void> validateIdentityDocumentUnique(String identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument)
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new UserAlreadyExistsException()))
                .then();
    }
}