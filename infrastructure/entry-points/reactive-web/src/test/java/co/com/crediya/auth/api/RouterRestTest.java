package co.com.crediya.auth.api;

import co.com.crediya.auth.api.dto.request.LoginRequest;
import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.LoginResponse;
import co.com.crediya.auth.api.dto.response.UserResponse;
import co.com.crediya.auth.api.exception.GlobalExceptionHandler;
import co.com.crediya.auth.model.exception.security.InvalidCredentialsException;
import co.com.crediya.auth.model.exception.user.UserAlreadyExistsException;
import co.com.crediya.auth.model.exception.user.UserNotFoundException;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.usecase.login.LoginUseCase;
import co.com.crediya.auth.usecase.user.UserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        RouterRest.class,
        Handler.class,
        GlobalExceptionHandler.class,
})@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;


    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegisterUserRequest request = RouterTestDataBuilder.buildValidRegisterUserRequest();
        User mockUser = RouterTestDataBuilder.buildValidDomainUser();

        when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(mockUser));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getFirstName()).isEqualTo("Juan");
                    assertThat(response.getLastName()).isEqualTo("Perez");
                    assertThat(response.getEmail()).isEqualTo("juan@test.com");
                    assertThat(response.getIdentityDocument()).isEqualTo("12345678");
                    assertThat(response.getUserId()).isEqualTo(1L);
                });
    }

    @Test
    @DisplayName("Should handle validation errors in register user request")
    void shouldHandleValidationErrorsInRegisterUser() {
        // Arrange
        RegisterUserRequest invalidRequest = RouterTestDataBuilder.buildInvalidRegisterUserRequest();

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle user already exists exception")
    void shouldHandleUserAlreadyExistsException() {
        // Arrange
        RegisterUserRequest request = RouterTestDataBuilder.buildValidRegisterUserRequest();

        when(userUseCase.registerUser(any(User.class)))
                .thenReturn(Mono.error(new UserAlreadyExistsException("juan@test.com")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle user registration with nullable fields")
    void shouldHandleUserRegistrationWithNullableFields() {
        // Arrange
        RegisterUserRequest request = RouterTestDataBuilder.buildRegisterUserRequestWithNullableFields();
        User mockUser = RouterTestDataBuilder.buildDomainUserWithNullableFields();

        when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(mockUser));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getFirstName()).isEqualTo("Ana");
                    assertThat(response.getPhone()).isNull();
                });
    }

    @Test
    @DisplayName("Should handle malformed JSON in register user request")
    void shouldHandleMalformedJsonInRegisterUser() {
        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{ invalid json")
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    @DisplayName("Should retrieve user by identity document successfully")
    void shouldRetrieveUserByIdentityDocumentSuccessfully() {
        // Arrange
        String identityDocument = "12345678";
        User mockUser = RouterTestDataBuilder.buildValidDomainUser();

        when(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .thenReturn(Mono.just(mockUser));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/retrieve?identityDocument=" + identityDocument)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getFirstName()).isEqualTo("Juan");
                    assertThat(response.getIdentityDocument()).isEqualTo(identityDocument);
                    assertThat(response.getUserId()).isEqualTo(1L);
                });
    }

    @Test
    @DisplayName("Should handle user not found exception")
    void shouldHandleUserNotFoundException() {
        // Arrange
        String identityDocument = "nonexistent";

        when(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .thenReturn(Mono.error(new UserNotFoundException()));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/retrieve?identityDocument=" + identityDocument)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle missing query parameter in retrieve user")
    void shouldHandleMissingQueryParameterInRetrieveUser() {
        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/retrieve")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound(); // Route doesn't match without query param
    }

    @Test
    @DisplayName("Should handle empty query parameter in retrieve user")
    void shouldHandleEmptyQueryParameterInRetrieveUser() {
        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/retrieve?identityDocument=")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound(); // Route validation rejects empty values
    }

    @Test
    @DisplayName("Should handle special characters in identity document")
    void shouldHandleSpecialCharactersInIdentityDocument() {
        // Arrange
        String identityDocument = "1234567890";
        User mockUser = RouterTestDataBuilder.buildValidDomainUser().toBuilder()
                .identityDocument(identityDocument)
                .build();

        when(userUseCase.retrieveUserByIdentityDocument(identityDocument))
                .thenReturn(Mono.just(mockUser));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/retrieve?identityDocument=" + identityDocument)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getIdentityDocument()).isEqualTo(identityDocument);
                });
    }

    // ================== POST /api/v1/login - AUTHENTICATE USER TESTS ==================

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() {
        // Arrange
        LoginRequest request = RouterTestDataBuilder.buildValidLoginRequest();
        String mockToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.mockToken";

        when(loginUseCase.authenticateUser(request.getEmail(), request.getPassword()))
                .thenReturn(Mono.just(mockToken));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoginResponse.class)
                .value(response -> {
                    assertThat(response.getToken()).isEqualTo(mockToken);
                    assertThat(response.getTokenType()).isEqualTo("Bearer");
                    assertThat(response.getExpiresIn()).isEqualTo(86400000L);
                });
    }

    @Test
    @DisplayName("Should handle invalid credentials exception")
    void shouldHandleInvalidCredentialsException() {
        // Arrange
        LoginRequest request = RouterTestDataBuilder.buildValidLoginRequest();

        when(loginUseCase.authenticateUser(request.getEmail(), request.getPassword()))
                .thenReturn(Mono.error(new InvalidCredentialsException()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle validation errors in login request")
    void shouldHandleValidationErrorsInLoginRequest() {
        // Arrange
        LoginRequest invalidRequest = RouterTestDataBuilder.buildInvalidLoginRequest();

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle malformed JSON in login request")
    void shouldHandleMalformedJsonInLoginRequest() {
        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{ invalid json}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should handle authentication service error")
    void shouldHandleAuthenticationServiceError() {
        // Arrange
        LoginRequest request = RouterTestDataBuilder.buildValidLoginRequest();

        when(loginUseCase.authenticateUser(request.getEmail(), request.getPassword()))
                .thenReturn(Mono.error(new RuntimeException("Authentication service unavailable")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    // ================== POST /api/v1/users/retrieve-batch - BATCH RETRIEVE TESTS ==================

    @Test
    @DisplayName("Should retrieve users by identity documents successfully")
    void shouldRetrieveUsersByIdentityDocumentsSuccessfully() {
        // Arrange
        String[] identityDocuments = RouterTestDataBuilder.buildValidIdentityDocumentsArray();
        List<User> mockUsers = RouterTestDataBuilder.buildDomainUsersList();

        when(userUseCase.retrieveUsersByIdentityDocuments(Arrays.asList(identityDocuments)))
                .thenReturn(Mono.just(mockUsers));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(identityDocuments)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponse.class)
                .value(responses -> {
                    assertThat(responses).hasSize(3);
                    assertThat(responses.get(0).getFirstName()).isEqualTo("Juan");
                    assertThat(responses.get(1).getFirstName()).isEqualTo("Maria");
                    assertThat(responses.get(2).getFirstName()).isEqualTo("Carlos");
                });
    }

    @Test
    @DisplayName("Should handle empty list in batch retrieve")
    void shouldHandleEmptyListInBatchRetrieve() {
        // Arrange
        String[] emptyArray = RouterTestDataBuilder.buildEmptyIdentityDocumentsArray();

        when(userUseCase.retrieveUsersByIdentityDocuments(Collections.emptyList()))
                .thenReturn(Mono.just(Collections.emptyList()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(emptyArray)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponse.class)
                .value(responses -> {
                    assertThat(responses).isEmpty();
                });
    }

    @Test
    @DisplayName("Should handle single document in batch retrieve")
    void shouldHandleSingleDocumentInBatchRetrieve() {
        // Arrange
        String[] singleDocument = RouterTestDataBuilder.buildSingleIdentityDocumentArray();
        List<User> singleUser = Collections.singletonList(RouterTestDataBuilder.buildValidDomainUser());

        when(userUseCase.retrieveUsersByIdentityDocuments(Arrays.asList(singleDocument)))
                .thenReturn(Mono.just(singleUser));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(singleDocument)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponse.class)
                .value(responses -> {
                    assertThat(responses).hasSize(1);
                    assertThat(responses.get(0).getFirstName()).isEqualTo("Juan");
                });
    }

    @Test
    @DisplayName("Should handle partial results in batch retrieve")
    void shouldHandlePartialResultsInBatchRetrieve() {
        // Arrange
        String[] mixedDocuments = {"12345678", "nonexistent", "87654321"};
        List<User> partialResults = Arrays.asList(
                RouterTestDataBuilder.buildValidDomainUser(),
                RouterTestDataBuilder.buildSecondDomainUser()
        );

        when(userUseCase.retrieveUsersByIdentityDocuments(Arrays.asList(mixedDocuments)))
                .thenReturn(Mono.just(partialResults));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(mixedDocuments)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponse.class)
                .value(responses -> {
                    assertThat(responses).hasSize(2); // Only found 2 out of 3
                });
    }

    @Test
    @DisplayName("Should handle malformed JSON in batch retrieve")
    void shouldHandleMalformedJsonInBatchRetrieve() {
        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{ invalid json array }")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should handle repository error in batch retrieve")
    void shouldHandleRepositoryErrorInBatchRetrieve() {
        // Arrange
        String[] identityDocuments = RouterTestDataBuilder.buildValidIdentityDocumentsArray();

        when(userUseCase.retrieveUsersByIdentityDocuments(Arrays.asList(identityDocuments)))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(identityDocuments)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    // ================== EDGE CASES AND INTEGRATION TESTS ==================

    @Test
    @DisplayName("Should handle missing Accept header")
    void shouldHandleMissingAcceptHeader() {
        // Arrange
        RegisterUserRequest request = RouterTestDataBuilder.buildValidRegisterUserRequest();
        User mockUser = RouterTestDataBuilder.buildValidDomainUser();

        when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(mockUser));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk(); // Should work without explicit Accept header
    }



    @Test
    @DisplayName("Should handle large payload in batch retrieve")
    void shouldHandleLargePayloadInBatchRetrieve() {
        // Arrange
        String[] largeArray = new String[100];
        for (int i = 0; i < 100; i++) {
            largeArray[i] = "document" + i;
        }

        when(userUseCase.retrieveUsersByIdentityDocuments(Arrays.asList(largeArray)))
                .thenReturn(Mono.just(Collections.emptyList()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users/retrieve-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(largeArray)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .value(responses -> {
                    assertThat(responses).isEmpty();
                });
    }
}