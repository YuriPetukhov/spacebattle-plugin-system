package org.spacebattle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.io.InputStream;
import java.util.Map;

/**
 * Обработчик HTTP-запросов для регистрации объектов в игре.
 * Поддерживает только POST-запросы. В теле запроса ожидается JSON с данными для регистрации.
 * После успешной обработки возвращает JSON-ответ с результатом.
 *
 * В случае ошибки вызывает {@link ExceptionHandler} и возвращает ответ с кодом 500.
 */
public class GameRegisterHandler implements HttpHandler {

    private final ObjectMapper mapper;
    private final GameRegistrationProcessor processor;
    private final CommandResponseSender responseSender;
    private final ExceptionHandler exceptionHandler;

    /**
     * Конструктор обработчика.
     *
     * @param ioc              IoC-контейнер для регистрации объектов
     * @param exceptionHandler обработчик исключений
     * @param mapper           Jackson-сериализатор/десериализатор
     */
    public GameRegisterHandler(IoC ioc, ExceptionHandler exceptionHandler, ObjectMapper mapper) {
        this.mapper = mapper;
        this.processor = new GameRegistrationProcessor(ioc);
        this.responseSender = new CommandResponseSender(mapper); // можно переиспользовать
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Обрабатывает HTTP-запрос на регистрацию игры.
     * Ожидает POST-запрос с JSON-телом и возвращает JSON-ответ.
     *
     * @param exchange HTTP-соединение
     */
    @Override
    public void handle(HttpExchange exchange) {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            responseSender.sendEmpty(exchange, 405);
            return;
        }

        try (InputStream is = exchange.getRequestBody()) {
            Map<String, Object> raw = mapper.readValue(is, new TypeReference<>() {});
            Map<String, Object> result = processor.register(raw);
            responseSender.send(exchange, 200, result);
        } catch (Exception e) {
            exceptionHandler.handle(getClass().getSimpleName(), e);
            responseSender.send(exchange, 500, Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
