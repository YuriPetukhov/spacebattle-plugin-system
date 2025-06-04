package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;

import static org.mockito.Mockito.*;

class ExceptionCatchingMiddlewareTest {

    @Test
    void testCommandExecutesWithoutException() throws Exception {
        Command command = mock(Command.class);
        ExceptionHandler handler = mock(ExceptionHandler.class);

        ExceptionCatchingMiddleware middleware = new ExceptionCatchingMiddleware(handler);
        Command wrapped = middleware.wrap(command);

        wrapped.execute();

        verify(command).execute();
        verifyNoInteractions(handler);
    }

    @Test
    void testCommandThrowsExceptionAndIsHandled() throws Exception {
        Command command = mock(Command.class);
        ExceptionHandler handler = mock(ExceptionHandler.class);

        RuntimeException exception = new RuntimeException("Something went wrong");
        doThrow(exception).when(command).execute();

        ExceptionCatchingMiddleware middleware = new ExceptionCatchingMiddleware(handler);
        Command wrapped = middleware.wrap(command);

        wrapped.execute();

        verify(command).execute();
        verify(handler).handle(command.getClass().getSimpleName(), exception);
    }
}
