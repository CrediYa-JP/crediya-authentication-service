package co.com.crediya.auth.usecase.user;

import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import co.com.crediya.auth.usecase.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Should register user successfully with valid data")
    void shouldRegisterUserSuccessfully() {
        User inputUser = TestDataBuilder.buildValidUser();
        User savedUser = TestDataBuilder.buildSavedUser();
        String hashedPassword = "$2a$10$hashedPassword123";

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(registerUserUseCase.execute(inputUser))
                .expectNext(savedUser)
                .verifyComplete();

        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository).existsByEmail(inputUser.getEmail());
        verify(userRepository).existsByIdentityDocument(inputUser.getIdentityDocument());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        User inputUser = TestDataBuilder.buildValidUser();
        String hashedPassword = "$2a$10$hashedPassword123";

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(registerUserUseCase.execute(inputUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository).existsByEmail(inputUser.getEmail());
        verify(userRepository, never()).existsByIdentityDocument(any());
        verify(userRepository, never()).save(any());
    }
}