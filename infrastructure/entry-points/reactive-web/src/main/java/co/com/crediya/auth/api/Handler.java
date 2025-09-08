package co.com.crediya.auth.api;
import co.com.crediya.auth.api.dto.request.LoginRequest;
import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.LoginResponse;
import co.com.crediya.auth.api.mapper.UserMapper;
import co.com.crediya.auth.api.util.ValidationUtil;
import co.com.crediya.auth.model.user.User;
import co.com.crediya.auth.usecase.login.LoginUseCase;
import co.com.crediya.auth.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UserUseCase userUseCase;
    private final LoginUseCase loginUseCase;


    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(RegisterUserRequest.class)
                .flatMap(ValidationUtil::validate)
                .doOnNext(req -> log.info("AUTH_USER_REGISTER_REQUEST email={}", req.getEmail()))
                .map(UserMapper::toUser)
                .flatMap(userUseCase::registerUser)
                .map(UserMapper::toUserResponse)
                .flatMap(userResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse));

    }

    public Mono<ServerResponse> retrieveUsersByIdentityDocuments(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(String[].class)
                .map(Arrays::asList)
                .doOnNext(identityDocuments -> log.info("AUTH_BATCH_REQUEST identityDocuments={}", identityDocuments))
                .flatMap(identityDocuments -> {
                    // ✅ Debug explícito del tipo retornado
                    Mono<List<User>> usersResult = userUseCase.retrieveUsersByIdentityDocuments(identityDocuments);
                    return usersResult;
                })
                .flatMapMany(Flux::fromIterable)  // List<User> → Flux<User>
                .map(UserMapper::toUserResponse)  // User → UserResponse
                .collectList()                    // Flux<UserResponse> → Mono<List<UserResponse>>
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users));
    }

    public Mono<ServerResponse>retrieveUserByIdentityDocument(ServerRequest serverRequest){

        String identityDocument = serverRequest.queryParam("identityDocument").
                orElseThrow(() -> new IllegalArgumentException("identityDocument is required"));
        log.info("AUTH_USER_RETRIEVE_BY_IdentityDocument identityDocument={}", identityDocument);

        return userUseCase.retrieveUserByIdentityDocument(identityDocument)
                .map(UserMapper::toUserResponse)
                .flatMap(userResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse));
    }

    public Mono<ServerResponse> authenticateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(ValidationUtil::validate)
                .doOnNext(req -> log.info("AUTH_LOGIN_REQUEST email={}", req.getEmail()))
                .flatMap(request -> loginUseCase.authenticateUser(request.getEmail(), request.getPassword()))
                .map(token -> LoginResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .expiresIn(86400000L)
                        .build())
                .flatMap(loginResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginResponse));
    }
}