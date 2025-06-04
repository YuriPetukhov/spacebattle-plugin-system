package org.spacebattle.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Фильтр Gateway, логирующий HTTP-запросы при прохождении через шлюз.
 * Логирует метод и URI входящего запроса.
 */
@Component
public class RequestLoggerGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggerGatewayFilterFactory.class);

    /**
     * Конструктор с указанием типа конфигурации фильтра.
     * В данном случае конфигурация не используется (Object).
     */
    public RequestLoggerGatewayFilterFactory() {
        super(Object.class);
    }

    /**
     * Реализация метода фильтра, логирующего входящий HTTP-запрос.
     *
     * @param config объект конфигурации фильтра (не используется)
     * @return GatewayFilter, который логирует метод и URI запроса
     */
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerWebExchange request = exchange;
            log.info("➡ Request: {} {}", request.getRequest().getMethod(), request.getRequest().getURI());
            return chain.filter(exchange);
        };
    }
}
