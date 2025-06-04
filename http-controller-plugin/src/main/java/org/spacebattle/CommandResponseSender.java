package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;

/**
 * Утилитный класс для отправки HTTP-ответов клиенту.
 * Используется для сериализации объекта в JSON и возврата HTTP-статуса.
 */
public class CommandResponseSender {

    private final ObjectMapper mapper;

    /**
     * Конструктор принимает Jackson ObjectMapper для сериализации.
     *
     * @param mapper сериализатор JSON
     */
    public CommandResponseSender(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Отправляет JSON-ответ клиенту с заданным HTTP-статусом и объектом-результатом.
     *
     * @param exchange HTTP-соединение
     * @param status   HTTP-статус ответа (например, 200, 400, 500)
     * @param body     объект, сериализуемый в JSON
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
     * Отправляет пустой HTTP-ответ с заданным статусом (например, 405 Method Not Allowed).
     *
     * @param exchange HTTP-соединение
     * @param status   HTTP-статус ответа
     */
    public void sendEmpty(HttpExchange exchange, int status) {
        try {
            exchange.sendResponseHeaders(status, -1);
        } catch (Exception ignored) {
        }
    }
}
