package co.com.crediya.auth.usecase.user;


import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> registerUser(User user) {
        return validateBusinessRules(user)
                .then(userRepository.save(user));
    }

    private Mono<Void> validateBusinessRules(User user) {
        return validateEmailUnique(user.getEmail());
    }

    private Mono<Void> validateEmailUnique(String email) {
        return userRepository.existsByEmail(email)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new UserAlreadyExistsException(email))
                        : Mono.empty());
    }
}