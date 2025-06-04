package org.spacebattle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * HTTP-обработчик команд, получаемых от клиента через POST-запрос на /command.
 * Поддерживает как одиночную команду, так и список команд в формате JSON.
 */
public class CommandHttpHandler implements HttpHandler {

    private final IoC ioc;
    private final ExceptionHandler exceptionHandler;
    private final ObjectMapper mapper;
    private final CommandProcessor processor;
    private final CommandResponseSender responseSender;

    /**
     * Конструктор обработчика HTTP-запросов команд.
     *
     * @param ioc              IoC-контейнер для внедрения зависимостей
     * @param exceptionHandler обработчик исключений
     * @param mapper           сериализатор/десериализатор JSON
     */
    public CommandHttpHandler(IoC ioc, ExceptionHandler exceptionHandler, ObjectMapper mapper) {
        this.ioc = ioc;
        this.exceptionHandler = exceptionHandler;
        this.mapper = mapper;
        this.processor = new CommandProcessor(ioc);
        this.responseSender = new CommandResponseSender(mapper);
    }

    /**
     * Обрабатывает входящий HTTP-запрос:
     * - Поддерживает только POST-запросы;
     * - Разбирает JSON с одной или несколькими командами;
     * - Выполняет команды и возвращает ответ;
     * - При ошибках возвращает 400 или 500.
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
            JsonNode jsonNode = mapper.readTree(is);

            if (jsonNode.isArray()) {
                List<Map<String, Object>> rawList = mapper.convertValue(jsonNode, new TypeReference<>() {});
                for (Map<String, Object> raw : rawList) {
                    CommandDTO dto = CommandDTO.fromMap(raw);
                    processor.process(dto);
                }
                responseSender.send(exchange, 200, Map.of("status", "ok", "message", "Команды выполнены"));
            } else {
                Map<String, Object> raw = mapper.convertValue(jsonNode, new TypeReference<>() {});
                CommandDTO dto = CommandDTO.fromMap(raw);
                Map<String, Object> result = processor.process(dto);
                responseSender.send(exchange, 200, result);
            }
        } catch (ClientInputException e) {
            exceptionHandler.handle(getClass().getSimpleName(), e);
            responseSender.send(exchange, 400, Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            exceptionHandler.handle(getClass().getSimpleName(), e);
            responseSender.send(exchange, 500, Map.of("status", "error", "message", "Внутренняя ошибка"));
        }
    }
}
