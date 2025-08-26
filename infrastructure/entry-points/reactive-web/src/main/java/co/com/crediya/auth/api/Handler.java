package co.com.crediya.auth.api;
import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.UserResponse;
import co.com.crediya.auth.api.exception.ApiErrorResponse;
import co.com.crediya.auth.api.mapper.UserMapper;
import co.com.crediya.auth.api.util.ValidationUtil;
import co.com.crediya.auth.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UserUseCase userUseCase;

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
}