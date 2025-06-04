package org.spacebattle;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommandControllerServerTest {

    private IoC ioc;
    private ExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        exceptionHandler = mock(ExceptionHandler.class);
    }

    @Test
    void start_shouldCreateAndStartHttpServer() throws Exception {
        HttpServer httpServerMock = mock(HttpServer.class);

        try (MockedStatic<HttpServer> mockedStatic = mockStatic(HttpServer.class)) {
            mockedStatic
                    .when(() -> HttpServer.create(any(InetSocketAddress.class), eq(0)))
                    .thenReturn(httpServerMock);

            CommandControllerServer server = new CommandControllerServer(ioc, exceptionHandler);
            server.start(8080);

            verify(httpServerMock).createContext(eq("/command"), any(CommandHttpHandler.class));
            verify(httpServerMock).setExecutor(null);
            verify(httpServerMock).start();
        }
    }

    @Test
    void start_shouldThrowExceptionOnInvalidPort() {
        CommandControllerServer server = new CommandControllerServer(ioc, exceptionHandler);

        assertThrows(Exception.class, () -> server.start(-1)); // Недопустимый порт
    }
}
