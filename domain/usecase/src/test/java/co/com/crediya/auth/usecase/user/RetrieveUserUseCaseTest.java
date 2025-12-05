package co.com.crediya.auth.usecase.user;

import co.com.crediya.auth.model.exception.user.UserNotFoundException;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrieveUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RetrieveUserUseCase retrieveUserUseCase;

    @Test
    @DisplayName("Should return user when found by identity document")
    void shouldRetrieveUserByIdentityDocument() {
        String identityDocument = "12345678";
        User foundUser = TestDataBuilder.buildSavedUser();

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.just(foundUser));

        StepVerifier.create(retrieveUserUseCase.executeByIdentityDocument(identityDocument))
                .expectNext(foundUser)
                .verifyComplete();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        String identityDocument = "nonexistent";

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.empty());

        StepVerifier.create(retrieveUserUseCase.executeByIdentityDocument(identityDocument))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Should return list of users by identity documents")
    void shouldRetrieveUsersByIdentityDocuments() {
        List<String> identityDocuments = Arrays.asList("12345678", "87654321");
        List<User> users = Arrays.asList(
                TestDataBuilder.buildSavedUser(),
                TestDataBuilder.buildSavedUser()
        );

        when(userRepository.findByIdentityDocuments(identityDocuments)).thenReturn(Mono.just(users));

        StepVerifier.create(retrieveUserUseCase.executeByIdentityDocuments(identityDocuments))
                .expectNext(users)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(identityDocuments);
    }
}