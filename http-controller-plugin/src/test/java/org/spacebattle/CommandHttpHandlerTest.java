package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class CommandHttpHandlerTest {

    private IoC ioc;
    private ExceptionHandler exceptionHandler;
    private ObjectMapper mapper;
    private CommandHttpHandler handler;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        exceptionHandler = mock(ExceptionHandler.class);
        mapper = new ObjectMapper();
        handler = new CommandHttpHandler(ioc, exceptionHandler, mapper);
    }

    private void injectProcessorMock(CommandProcessor processorMock) throws Exception {
        Field processorField = CommandHttpHandler.class.getDeclaredField("processor");
        processorField.setAccessible(true);
        processorField.set(handler, processorMock);
    }

    @Test
    void handle_shouldReturn405ForNonPost() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
    }

    @Test
    void handle_shouldProcessSingleCommand() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(output);

        String json = """
                {
                  "objectId": "ship-1",
                  "action": "move",
                  "params": { "dx": 1, "dy": 1 }
                }
                """;
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);

        CommandProcessor processorMock = mock(CommandProcessor.class);
        when(processorMock.process(any())).thenReturn(Map.of("status", "ok"));
        injectProcessorMock(processorMock);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void handle_shouldProcessCommandList() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(output);

        String json = """
                [
                  { "objectId": "ship-1", "action": "move", "params": { "dx": 1, "dy": 1 } },
                  { "objectId": "ship-2", "action": "rotate", "params": { "angle": 90 } }
                ]
                """;
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);

        CommandProcessor processorMock = mock(CommandProcessor.class);
        when(processorMock.process(any())).thenReturn(Map.of("status", "ok"));
        injectProcessorMock(processorMock);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void handle_shouldReturn400OnClientError() throws Exception {
        // Мокаем HTTP-запрос
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(output);

        // JSON с ошибочной командой
        String json = """
                { "objectId": "ship-1", "action": "invalid", "params": {} }
                """;

        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);

        // Подменяем processor мок-объектом
        CommandProcessor processorMock = mock(CommandProcessor.class);
        when(processorMock.process(any())).thenThrow(new ClientInputException("Invalid command"));
        injectProcessorMock(processorMock); // reflection

        // Вызываем обработчик
        handler.handle(exchange);

        // Проверки
        verify(exchange).sendResponseHeaders(eq(400), anyLong());
        verify(exceptionHandler).handle(eq("CommandHttpHandler"), any(ClientInputException.class));
    }


    @Test
    void handle_shouldReturn500OnUnexpectedError() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(output);

        String json = """
                { "objectId": "ship-1", "command": "crash" }
                """;
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);

        CommandProcessor processorMock = mock(CommandProcessor.class);
        when(processorMock.process(any())).thenThrow(new RuntimeException("Internal error"));
        injectProcessorMock(processorMock);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(500), anyLong());
        verify(exceptionHandler).handle(eq("CommandHttpHandler"), any(RuntimeException.class));
    }
}
