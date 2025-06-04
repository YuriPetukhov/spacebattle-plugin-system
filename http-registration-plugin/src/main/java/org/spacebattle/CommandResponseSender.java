package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;

/**
 * Класс отвечает за отправку HTTP-ответов клиенту.
 * Используется в HTTP-контроллерах для сериализации тела ответа и отправки статуса.
 */
public class CommandResponseSender {

    private final ObjectMapper mapper;

    /**
     * Конструктор принимает {@link ObjectMapper} — общий сериализатор JSON.
     *
     * @param mapper Jackson ObjectMapper
     */
    public CommandResponseSender(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Отправляет JSON-ответ с заданным HTTP-статусом и телом.
     *
     * @param exchange HTTP-контекст
     * @param status   HTTP-статус (например, 200, 400, 500)
     * @param body     объект, который будет сериализован в JSON
     */
    public void send(HttpExchange exchange, int status, Object body) {
        try {
            byte[] json = mapper.writeValueAsBytes(body);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, json.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет пустой ответ с указанным статусом (например, 405 Method Not Allowed).
     *
     * @param exchange HTTP-контекст
     * @param status   HTTP-статус
     */
    public void sendEmpty(HttpExchange exchange, int status) {
        try {
            exchange.sendResponseHeaders(status, -1);
        } catch (Exception ignored) {}
    }
}
