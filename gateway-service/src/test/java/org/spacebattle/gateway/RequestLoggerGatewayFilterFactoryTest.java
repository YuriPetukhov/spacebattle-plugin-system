package org.spacebattle.gateway;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class RequestLoggerGatewayFilterFactoryTest {

    @Test
    void testFilterLogsRequest() {
        var factory = new RequestLoggerGatewayFilterFactory();
        var filter = factory.apply(new Object());


        var request = MockServerHttpRequest.get("/test/path").build();
        var exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);
        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        assertNotNull(result);
        result.block();

        Mockito.verify(chain).filter(exchange);
    }
}
