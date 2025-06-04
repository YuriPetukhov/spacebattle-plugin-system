package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommandControllerBootstrapTest {

    private IoC ioc;
    private ExceptionHandler exceptionHandler;
    private CommandControllerBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        exceptionHandler = mock(ExceptionHandler.class);
        bootstrap = new CommandControllerBootstrap();
        bootstrap.setIoC(ioc);
    }

    @Test
    void run_shouldStartServerSuccessfully() throws Exception {
        when(ioc.resolve("ioc")).thenReturn(ioc);
        when(ioc.resolve("exception-handler")).thenReturn(exceptionHandler);

        try (MockedConstruction<CommandControllerServer> mockServer =
                     mockConstruction(CommandControllerServer.class,
                             (mock, context) -> doNothing().when(mock).start(8080))) {

            assertDoesNotThrow(() -> bootstrap.run());

            CommandControllerServer server = mockServer.constructed().get(0);
            verify(server).start(8080);
        }
    }

    @Test
    void run_shouldHandleExceptionWhenStartFails() throws Exception {
        when(ioc.resolve("ioc")).thenReturn(ioc);
        when(ioc.resolve("exception-handler")).thenReturn(exceptionHandler);

        RuntimeException exception = new RuntimeException("Startup error");

        try (MockedConstruction<CommandControllerServer> mockServer =
                     mockConstruction(CommandControllerServer.class,
                             (mock, context) -> doThrow(exception).when(mock).start(8080))) {

            bootstrap.run();

            verify(exceptionHandler).handle("CommandControllerBootstrap", exception);
        }
    }
}
