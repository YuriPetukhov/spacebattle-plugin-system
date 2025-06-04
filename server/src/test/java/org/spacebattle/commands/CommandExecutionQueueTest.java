package org.spacebattle.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandExecutionQueueTest {

    private CommandExecutionQueue queue;

    @AfterEach
    void tearDown() {
        if (queue != null) {
            queue.stop();
        }
    }

    @Test
    void testCommandIsExecuted() throws Exception {
        Command command = mock(Command.class);
        ExceptionHandler handler = mock(ExceptionHandler.class);
        queue = new CommandExecutionQueue(handler);

        queue.submit(command);

        Thread.sleep(50);

        verify(command, atLeastOnce()).execute();
        verifyNoInteractions(handler);
    }

    @Test
    void testCommandThrowsExceptionHandled() throws Exception {
        Command command = mock(Command.class);
        doThrow(new RuntimeException("Fail")).when(command).execute();

        ExceptionHandler handler = mock(ExceptionHandler.class);
        queue = new CommandExecutionQueue(handler);

        queue.submit(command);

        Thread.sleep(50);

        verify(command).execute();
        verify(handler).handle(eq(command.getClass().getSimpleName()), any());
    }

    @Test
    void testQueueStopsGracefully() {
        ExceptionHandler handler = mock(ExceptionHandler.class);
        queue = new CommandExecutionQueue(handler);

        queue.stop();

        assertFalse(Thread.currentThread().isInterrupted());
    }
}
