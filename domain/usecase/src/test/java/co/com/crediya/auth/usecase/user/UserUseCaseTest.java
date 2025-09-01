package co.com.crediya.auth.usecase.user;

import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
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

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    @Test
    @DisplayName("Should register user successfully when email and document are unique")
    void shouldRegisterUserSuccessfully() {
        User inputUser = TestDataBuilder.buildValidUser();
        User savedUser = TestDataBuilder.buildSavedUser();

        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(inputUser)).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectNext(savedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowExceptionWhenEmailExists() {

        User inputUser = TestDataBuilder.buildValidUser();

        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(userRepository, never()).existsByIdentityDocument(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when identity document already exists")
    void shouldThrowExceptionWhenDocumentExists() {
        User inputUser = TestDataBuilder.buildValidUser();

        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return user when found by identity document")
    void shouldRetrieveUserByIdentityDocument() {
        String identityDocument = "123456789";
        User foundUser = TestDataBuilder.buildSavedUser();

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.just(foundUser));

        StepVerifier.create(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .expectNext(foundUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate repository save error")
    void shouldPropagateRepositorySaveError() {

        User inputUser = TestDataBuilder.buildValidUser();
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(inputUser)).thenReturn(Mono.error(repositoryError));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(RuntimeException.class)
                .verify();
    }
}