package co.com.crediya.auth.api;

import co.com.crediya.auth.api.dto.request.LoginRequest;
import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.LoginResponse;
import co.com.crediya.auth.api.mapper.UserMapper;
import co.com.crediya.auth.api.util.ValidationUtil;
import co.com.crediya.auth.usecase.login.LoginUseCase;
import co.com.crediya.auth.usecase.user.RegisterUserUseCase;
import co.com.crediya.auth.usecase.user.RetrieveUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCase registerUserUseCase;
    private final RetrieveUserUseCase retrieveUserUseCase;
    private final LoginUseCase loginUseCase;

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterUserRequest.class)
                .flatMap(ValidationUtil::validate)
                .map(UserMapper::toUser)
                .flatMap(registerUserUseCase::execute)
                .map(UserMapper::toUserResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }


    public Mono<ServerResponse> retrieveUserByIdentityDocument(ServerRequest serverRequest) {
        String identityDocument = serverRequest.queryParam("identityDocument")
                .orElseThrow(() -> new IllegalArgumentException("identityDocument is required"));
        return retrieveUserUseCase.executeByIdentityDocument(identityDocument)
                .map(UserMapper::toUserResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> retrieveUsersByIdentityDocuments(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(String[].class)
                .map(Arrays::asList)
                .flatMapMany(retrieveUserUseCase::executeByIdentityDocuments)
                .map(users-> users.stream()
                            .map(UserMapper::toUserResponse)
                            .toList()
                )
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users));
    }

    public Mono<ServerResponse> authenticateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(ValidationUtil::validate)
                .flatMap(request -> loginUseCase.execute(request.getEmail(), request.getPassword()))
                .map(token -> LoginResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .expiresIn(86400000L)
                        .build())
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}