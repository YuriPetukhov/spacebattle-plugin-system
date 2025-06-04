package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.dsl.DslLoader;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class GameRegisterHandlerTest {

    private ObjectMapper mapper;
    private ExceptionHandler exceptionHandler;
    private IoC ioc;
    private GameRegisterHandler handler;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        exceptionHandler = mock(ExceptionHandler.class);
        ioc = mock(IoC.class);
        handler = new GameRegisterHandler(ioc, exceptionHandler, mapper);
    }

    @Test
    void handle_shouldReturn405ForNonPost() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(405, -1);
    }

    @Test
    void handle_shouldReturn500AndCallExceptionHandlerOnInvalidJson() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");

        // Заголовки и тело ответа
        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        // Некорректный JSON
        InputStream is = new ByteArrayInputStream("invalid".getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(500), anyLong());
        verify(exceptionHandler).handle(eq("GameRegisterHandler"), any());

        String responseBody = os.toString(StandardCharsets.UTF_8);
        assert responseBody.contains("\"status\":\"error\"");
    }

    @Test
    void handle_shouldReturn200OnValidRequest() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("POST");

        // Заголовки и тело ответа
        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(os);

        // Корректный JSON с минимальными полями
        String jsonInput = "{\"players\": [\"Alice\", \"Bob\"]}";
        InputStream is = new ByteArrayInputStream(jsonInput.getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);

        DslLoader<Void> mockLoader = mock(DslLoader.class);
        when(ioc.resolve("dsl-object-loader")).thenReturn(mockLoader);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());

        String responseBody = os.toString(StandardCharsets.UTF_8);
        assert responseBody.contains("\"token\"");
    }
}
