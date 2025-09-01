package co.com.crediya.auth.api;

import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.UserResponse;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;


    @MockitoBean
    private UserUseCase userUseCase;


    @Test
    void testListenPOSTUseCase() {

        User mockUser = User.builder()
                .userId(1L)
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .build();

        when(userUseCase.registerUser(any(User.class)))
                .thenReturn(Mono.just(mockUser));

        RegisterUserRequest request = new RegisterUserRequest(
                "Juan", "Perez", "juan@test.com", "12345", "300123",
                new BigDecimal("5000000"), "password123");

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
                    Assertions.assertThat(response.getFirstName()).isEqualTo("Juan");
                    Assertions.assertThat(response.getEmail()).isEqualTo("juan@test.com");
                });
    }
}
