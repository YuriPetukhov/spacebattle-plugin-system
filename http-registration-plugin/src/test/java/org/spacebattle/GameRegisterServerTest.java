package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GameRegisterServerTest {

    @Test
    void start_shouldCreateAndStartHttpServer() throws Exception {
        IoC ioc = mock(IoC.class);
        ExceptionHandler handler = mock(ExceptionHandler.class);

        HttpServer httpServerMock = mock(HttpServer.class);

        try (MockedStatic<HttpServer> mockedStatic = mockStatic(HttpServer.class)) {
            mockedStatic
                    .when(() -> HttpServer.create(any(InetSocketAddress.class), eq(0)))
                    .thenReturn(httpServerMock);

            GameRegisterServer server = new GameRegisterServer(ioc, handler);
            server.start(8084);

            verify(httpServerMock).createContext(eq("/register"), any(GameRegisterHandler.class));
            verify(httpServerMock).setExecutor(null);
            verify(httpServerMock).start();
        }
    }

    @Test
    void start_shouldThrowExceptionAndNotCrash() {
        IoC ioc = mock(IoC.class);
        ExceptionHandler handler = mock(ExceptionHandler.class);

        GameRegisterServer server = new GameRegisterServer(ioc, handler);

        assertThrows(Exception.class, () -> {
            // Указываем заведомо недопустимый порт (например, -1), чтобы упала create()
            server.start(-1);
        });
    }
}
