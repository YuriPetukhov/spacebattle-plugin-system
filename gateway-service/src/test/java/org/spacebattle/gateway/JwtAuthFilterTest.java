package org.spacebattle.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthFilter();
    }

    @Test
    void testUnauthorizedWhenHeaderMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        JwtAuthFilter.Config config = new JwtAuthFilter.Config();
        Mono<Void> result = filter.apply(config).filter(exchange, exchange1 -> Mono.empty());

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testUnauthorizedWhenHeaderInvalidFormat() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Invalid token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        JwtAuthFilter.Config config = new JwtAuthFilter.Config();
        Mono<Void> result = filter.apply(config).filter(exchange, exchange1 -> Mono.empty());

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testValidHeaderProceedsAndMutatesHeader() {
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        JwtAuthFilter.Config config = new JwtAuthFilter.Config();

        Mono<Void> result = filter.apply(config).filter(exchange, exchange1 -> {
            assertEquals(token, exchange1.getRequest().getHeaders().getFirst("X-User-Token"));
            return Mono.empty();
        });

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }
}
