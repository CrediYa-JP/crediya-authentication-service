package co.com.crediya.auth.api.filter;

import co.com.crediya.auth.model.constants.RoleConstants;
import co.com.crediya.auth.security.JwtValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter implements WebFilter {

    private final JwtValidationUtil jwtValidationUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        if (shouldProtectEndpoint(path, method)) {
            return validateAdminOrAdvisorRole(exchange, chain);
        }

        return chain.filter(exchange);
    }

    private boolean shouldProtectEndpoint(String path, HttpMethod method) {
        return "/api/v1/users".equals(path) && HttpMethod.POST.equals(method);
    }

    private Mono<Void> validateAdminOrAdvisorRole(ServerWebExchange exchange, WebFilterChain chain) {
        return extractTokenFromHeader(exchange)
                .flatMap(this::validateTokenAndRole)
                .then(chain.filter(exchange))
                .onErrorResume(error -> unauthorizedResponse(exchange));
    }

    private Mono<String> extractTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Mono.just(authHeader.substring(7));
        }
        return Mono.error(new RuntimeException("No token provided"));
    }

    private Mono<Void> validateTokenAndRole(String token) {
        return Mono.fromCallable(() -> jwtValidationUtil.getRoleId(token))
                .filter(roleId -> roleId.equals(RoleConstants.ADMIN_ROLE_ID) || roleId.equals(RoleConstants.ADVISOR_ROLE_ID))
                .switchIfEmpty(Mono.error(new RuntimeException("Insufficient role")))
                .then();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}