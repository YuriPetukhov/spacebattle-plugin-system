package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandResponseSenderTest {

    private ObjectMapper objectMapper;
    private CommandResponseSender sender;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        sender = new CommandResponseSender(objectMapper);
    }

    @Test
    void send_shouldSendJsonWithStatusAndBody() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);
        Headers headers = new Headers();
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();

        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(responseBody);

        TestData data = new TestData("ok", 123);

        sender.send(exchange, 200, data);

        assertTrue(headers.containsKey("Content-Type"));
        assertEquals("application/json", headers.getFirst("Content-Type"));

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(exchange).getResponseBody();

        String responseString = responseBody.toString();
        assertTrue(responseString.contains("\"status\":\"ok\""));
        assertTrue(responseString.contains("\"code\":123"));
    }

    @Test
    void sendEmpty_shouldSendOnlyStatus() throws Exception {
        HttpExchange exchange = mock(HttpExchange.class);

        sender.sendEmpty(exchange, 405);

        verify(exchange).sendResponseHeaders(405, -1);
        verifyNoMoreInteractions(exchange);
    }

    static class TestData {
        public String status;
        public int code;

        public TestData(String status, int code) {
            this.status = status;
            this.code = code;
        }
    }
}
