package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.mockito.Mockito.*;

class CommandResponseSenderTest {

    private ObjectMapper mapper;
    private CommandResponseSender sender;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        sender = new CommandResponseSender(mapper);
    }

    @Test
    void send_shouldWriteJsonAndStatus() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        Headers headers = new Headers();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(os);

        Object response = new TestObject("value");
        sender.send(exchange, 200, response);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        assert os.toString().contains("\"field\":\"value\"");
        assert headers.containsKey("Content-Type");
    }

    @Test
    void sendEmpty_shouldSendStatusOnly() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);

        sender.sendEmpty(exchange, 405);

        verify(exchange).sendResponseHeaders(405, -1);
    }

    // Вспомогательный DTO-класс
    private record TestObject(String field) {}
}
