package co.com.crediya.auth.usecase.login;

import co.com.crediya.auth.model.exception.security.InvalidCredentialsException;
import co.com.crediya.auth.model.security.gateways.LoginGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private LoginGateway loginGateway;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() {
        // Arrange
        String email = "juan@test.com";
        String password = "password123";
        String expectedToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.mockToken";

        when(loginGateway.authenticateAndGenerateToken(email, password))
                .thenReturn(Mono.just(expectedToken));

        // Act & Assert
        StepVerifier.create(loginUseCase.authenticateUser(email, password))
                .expectNext(expectedToken)
                .verifyComplete();

        verify(loginGateway).authenticateAndGenerateToken(email, password);
    }

    @Test
    @DisplayName("Should propagate gateway authentication error")
    void shouldPropagateGatewayAuthenticationError() {
        // Arrange
        String email = "juan@test.com";
        String password = "wrongpassword";
        InvalidCredentialsException gatewayError = new InvalidCredentialsException();

        when(loginGateway.authenticateAndGenerateToken(email, password))
                .thenReturn(Mono.error(gatewayError));

        // Act & Assert
        StepVerifier.create(loginUseCase.authenticateUser(email, password))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(loginGateway).authenticateAndGenerateToken(email, password);
    }
}