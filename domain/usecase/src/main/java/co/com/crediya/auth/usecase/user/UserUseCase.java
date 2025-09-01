package co.com.crediya.auth.usecase.user;


import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
import co.com.crediya.auth.model.exception.user.UserNotFoundException;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public Mono<User> registerUser(User user) {
        User userWithHashedPassword = user.toBuilder()
                .password(passwordService.encode(user.getPassword()))
                .build();

        return validateEmailUnique(userWithHashedPassword.getEmail())
                .then(Mono.defer(() -> validateIdentityDocumentUnique(userWithHashedPassword.getIdentityDocument())))
                .then(Mono.defer(() -> userRepository.save(userWithHashedPassword)));
    }


    private Mono<Void> validateEmailUnique(String email) {
        return userRepository.existsByEmail(email)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new UserAlreadyExistsException(email))
                        : Mono.empty());
    }
    private Mono<Void> validateIdentityDocumentUnique(String identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new UserAlreadyExistsException())
                        : Mono.empty());
    }

    public Mono<User>retrieveUserByIdentityDocument(String identityDocument) {
        return userRepository.findByIdentityDocument(identityDocument)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }
}