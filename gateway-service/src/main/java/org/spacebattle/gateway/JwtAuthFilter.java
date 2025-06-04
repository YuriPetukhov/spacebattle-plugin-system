package org.spacebattle.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    /**
     * Конструктор фильтра — указывает класс конфигурации.
     */
    public JwtAuthFilter() {
        super(Config.class);
    }

    /**
     * Основная логика фильтра. Проверяет наличие заголовка Authorization и его формат.
     * Если заголовок валиден, извлекает токен и добавляет его в заголовок X-User-Token.
     * В противном случае возвращает статус 401 (Unauthorized).
     *
     * @param config конфигурация фильтра (пока не используется)
     * @return GatewayFilter
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder.header("X-User-Token", token))
                    .build();

            return chain.filter(mutatedExchange);
        };
    }

    /**
     * Конфигурация фильтра (может быть расширена в будущем).
     */
    public static class Config {
    }
}