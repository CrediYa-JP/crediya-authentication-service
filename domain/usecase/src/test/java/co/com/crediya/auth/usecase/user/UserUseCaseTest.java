package co.com.crediya.auth.usecase.user;

import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
import co.com.crediya.auth.model.exception.user.UserNotFoundException;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import co.com.crediya.auth.usecase.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserUseCase userUseCase;

    // ================== REGISTER USER TESTS ==================

    @Test
    @DisplayName("Should register user successfully when email and document are unique")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        User inputUser = TestDataBuilder.buildValidUser();
        User expectedSavedUser = TestDataBuilder.buildSavedUser();
        String hashedPassword = "$2a$10$hashedPassword123";

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(expectedSavedUser));

        // Act & Assert
        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectNext(expectedSavedUser)
                .verifyComplete();

        // Verify interactions
        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository).existsByEmail(inputUser.getEmail());
        verify(userRepository).existsByIdentityDocument(inputUser.getIdentityDocument());

        // Verify save was called with hashed password
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(hashedPassword, savedUser.getPassword());
        assertEquals(inputUser.getEmail(), savedUser.getEmail());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Arrange
        User inputUser = TestDataBuilder.buildValidUser();
        String hashedPassword = "$2a$10$hashedPassword123";

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository).existsByEmail(inputUser.getEmail());
        verify(userRepository, never()).existsByIdentityDocument(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when identity document already exists")
    void shouldThrowExceptionWhenDocumentExists() {

        User inputUser = TestDataBuilder.buildValidUser();
        String hashedPassword = "$2a$10$hashedPassword123";

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository).existsByEmail(inputUser.getEmail());
        verify(userRepository).existsByIdentityDocument(inputUser.getIdentityDocument());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle password service error during registration")
    void shouldHandlePasswordServiceError() {
        User inputUser = TestDataBuilder.buildValidUser();
        RuntimeException passwordError = new RuntimeException("Password encoding failed");

        when(passwordService.encode(inputUser.getPassword())).thenThrow(passwordError);

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(RuntimeException.class)
                .verify();

        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).existsByIdentityDocument(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate repository save error")
    void shouldPropagateRepositorySaveError() {

        User inputUser = TestDataBuilder.buildValidUser();
        String hashedPassword = "$2a$10$hashedPassword123";
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.error(repositoryError));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectError(RuntimeException.class)
                .verify();

        verify(passwordService).encode(inputUser.getPassword());
        verify(userRepository).existsByEmail(inputUser.getEmail());
        verify(userRepository).existsByIdentityDocument(inputUser.getIdentityDocument());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user with nullable fields during registration")
    void shouldRegisterUserWithNullableFields() {

        User inputUser = TestDataBuilder.buildUserWithNullableFields();
        User expectedSavedUser = inputUser.toBuilder().userId(1L).password("$2a$10$hashed").build();
        String hashedPassword = "$2a$10$hashed";

        when(passwordService.encode(inputUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(inputUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(expectedSavedUser));

        StepVerifier.create(userUseCase.registerUser(inputUser))
                .expectNext(expectedSavedUser)
                .verifyComplete();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertNull(savedUser.getPhone());
    }



    @Test
    @DisplayName("Should return user when found by identity document")
    void shouldRetrieveUserByIdentityDocument() {

        String identityDocument = "12345678";
        User foundUser = TestDataBuilder.buildSavedUser();

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.just(foundUser));

        StepVerifier.create(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .expectNext(foundUser)
                .verifyComplete();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by identity document")
    void shouldThrowUserNotFoundExceptionWhenUserNotFoundByDocument() {

        String identityDocument = "nonexistent";

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Should propagate repository error when finding by identity document")
    void shouldPropagateRepositoryErrorWhenFindingByDocument() {

        String identityDocument = "12345678";
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.error(repositoryError));


        StepVerifier.create(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Should handle special characters in identity document")
    void shouldHandleSpecialCharactersInIdentityDocument() {

        String identityDocument = "1234567890";  // Long document
        User foundUser = TestDataBuilder.buildSavedUser().toBuilder()
                .identityDocument(identityDocument)
                .build();

        when(userRepository.findByIdentityDocument(identityDocument)).thenReturn(Mono.just(foundUser));

        StepVerifier.create(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .expectNext(foundUser)
                .verifyComplete();

        verify(userRepository).findByIdentityDocument(identityDocument);
    }


    @Test
    @DisplayName("Should return list of users when found by identity documents")
    void shouldRetrieveUsersByIdentityDocuments() {

        List<String> identityDocuments = TestDataBuilder.buildValidIdentityDocumentsList();
        List<User> expectedUsers = TestDataBuilder.buildSavedUsersList();

        when(userRepository.findByIdentityDocuments(identityDocuments)).thenReturn(Mono.just(expectedUsers));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(identityDocuments))
                .expectNext(expectedUsers)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(identityDocuments);
    }

    @Test
    @DisplayName("Should return empty list when no users found by identity documents")
    void shouldReturnEmptyListWhenNoUsersFoundByDocuments() {

        List<String> identityDocuments = Arrays.asList("nonexistent1", "nonexistent2");
        List<User> emptyList = Collections.emptyList();

        when(userRepository.findByIdentityDocuments(identityDocuments)).thenReturn(Mono.just(emptyList));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(identityDocuments))
                .expectNext(emptyList)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(identityDocuments);
    }

    @Test
    @DisplayName("Should handle single identity document in list")
    void shouldHandleSingleIdentityDocumentInList() {
        List<String> singleDocument = Collections.singletonList("12345678");
        List<User> singleUser = Collections.singletonList(TestDataBuilder.buildSavedUser());

        when(userRepository.findByIdentityDocuments(singleDocument)).thenReturn(Mono.just(singleUser));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(singleDocument))
                .expectNext(singleUser)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(singleDocument);
    }

    @Test
    @DisplayName("Should handle empty identity documents list")
    void shouldHandleEmptyIdentityDocumentsList() {

        List<String> emptyList = Collections.emptyList();
        List<User> emptyUserList = Collections.emptyList();

        when(userRepository.findByIdentityDocuments(emptyList)).thenReturn(Mono.just(emptyUserList));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(emptyList))
                .expectNext(emptyUserList)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(emptyList);
    }

    @Test
    @DisplayName("Should propagate repository error when finding by identity documents")
    void shouldPropagateRepositoryErrorWhenFindingByDocuments() {

        List<String> identityDocuments = TestDataBuilder.buildValidIdentityDocumentsList();
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(userRepository.findByIdentityDocuments(identityDocuments)).thenReturn(Mono.error(repositoryError));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(identityDocuments))
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findByIdentityDocuments(identityDocuments);
    }

    @Test
    @DisplayName("Should handle partial results when some documents don't exist")
    void shouldHandlePartialResultsWhenSomeDocumentsDontExist() {

        List<String> mixedDocuments = Arrays.asList("12345678", "nonexistent", "87654321");
        List<User> partialResults = Arrays.asList(
                TestDataBuilder.buildSavedUser(),
                TestDataBuilder.buildSecondSavedUser()
        );

        when(userRepository.findByIdentityDocuments(mixedDocuments)).thenReturn(Mono.just(partialResults));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(mixedDocuments))
                .expectNext(partialResults)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(mixedDocuments);

        assertEquals(2, partialResults.size());
    }

    @Test
    @DisplayName("Should handle large list of identity documents")
    void shouldHandleLargeListOfIdentityDocuments() {

        List<String> largeList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        List<User> expectedUsers = Collections.singletonList(TestDataBuilder.buildSavedUser());

        when(userRepository.findByIdentityDocuments(largeList)).thenReturn(Mono.just(expectedUsers));

        StepVerifier.create(userUseCase.retrieveUsersByIdentityDocuments(largeList))
                .expectNext(expectedUsers)
                .verifyComplete();

        verify(userRepository).findByIdentityDocuments(largeList);
    }


    @Test
    @DisplayName("Should handle user with edge case data")
    void shouldHandleUserWithEdgeCaseData() {
        User edgeCaseUser = TestDataBuilder.buildUserWithEdgeCaseData();
        User savedEdgeCaseUser = edgeCaseUser.toBuilder().userId(99L).password("$2a$10$hashedA").build();
        String hashedPassword = "$2a$10$hashedA";

        when(passwordService.encode(edgeCaseUser.getPassword())).thenReturn(hashedPassword);
        when(userRepository.existsByEmail(edgeCaseUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(edgeCaseUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedEdgeCaseUser));

        StepVerifier.create(userUseCase.registerUser(edgeCaseUser))
                .expectNext(savedEdgeCaseUser)
                .verifyComplete();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("José María", savedUser.getFirstName());
        assertEquals("Rodríguez-Pérez", savedUser.getLastName());
        assertEquals("jose.maria@domain-test.com", savedUser.getEmail());
        assertEquals("", savedUser.getPhone()); // Empty string
    }
}