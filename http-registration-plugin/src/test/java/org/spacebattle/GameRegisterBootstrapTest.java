package org.spacebattle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import static org.mockito.Mockito.*;

class GameRegisterBootstrapTest {

    private IoC ioc;
    private ExceptionHandler exceptionHandler;
    private GameRegisterBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        exceptionHandler = mock(ExceptionHandler.class);
        bootstrap = new GameRegisterBootstrap();
        bootstrap.setIoC(ioc);

        when(ioc.resolve("ioc")).thenReturn(ioc);
        when(ioc.resolve("exception-handler")).thenReturn(exceptionHandler);
    }

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty("game.register.port");
    }

    @Test
    void run_shouldStartServerWithDefaultPort() throws Exception {
        try (var mocked = Mockito.mockConstruction(GameRegisterServer.class,
                (mock, context) -> doNothing().when(mock).start(8084))) {

            bootstrap.run();

            GameRegisterServer created = mocked.constructed().get(0);
            verify(created).start(8084);
        }
    }

    @Test
    void run_shouldStartServerWithCustomPortFromSystemProperty() throws Exception {
        System.setProperty("game.register.port", "9090");

        try (var mocked = Mockito.mockConstruction(GameRegisterServer.class,
                (mock, context) -> doNothing().when(mock).start(9090))) {

            bootstrap.run();

            GameRegisterServer created = mocked.constructed().get(0);
            verify(created).start(9090);
        }
    }

    @Test
    void run_shouldFallbackToDefaultPortIfPropertyInvalid() throws Exception {
        System.setProperty("game.register.port", "not-a-number");

        try (var mocked = Mockito.mockConstruction(GameRegisterServer.class,
                (mock, context) -> doNothing().when(mock).start(8084))) {

            bootstrap.run();

            GameRegisterServer created = mocked.constructed().get(0);
            verify(created).start(8084);
        }
    }

    @Test
    void run_shouldHandleExceptionOnStart() throws Exception {
        RuntimeException exception = new RuntimeException("Startup failed");

        try (var mocked = Mockito.mockConstruction(GameRegisterServer.class,
                (mock, context) -> doThrow(exception).when(mock).start(8084))) {

            bootstrap.run();

            verify(exceptionHandler).handle("GameRegisterBootstrap", exception);
        }
    }
}
