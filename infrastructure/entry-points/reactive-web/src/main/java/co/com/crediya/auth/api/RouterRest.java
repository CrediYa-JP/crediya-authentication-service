package co.com.crediya.auth.api;

import co.com.crediya.auth.api.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;

import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String API_V1_USERS = "/api/v1/users";

    @Bean
    @RouterOperation(
            path = "/api/v1/users",
            method = RequestMethod.POST,
            operation = @Operation(
                    operationId = "registerUser",
                    summary = "Register new user",
                    description = "Creates a new user in the system",
                    tags = {"User Management"},
                    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = RegisterUserRequest.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "User created",
                                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
                            @ApiResponse(responseCode = "400", description = "Validation error",
                                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                            @ApiResponse(responseCode = "409", description = "User already exists")
                    }
            )
    )
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(API_V1_USERS)
                        .and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)),
                handler::registerUser);
    }
}