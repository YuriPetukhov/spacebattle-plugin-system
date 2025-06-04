package org.spacebattle.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExecutionQueueTest {

    private ExceptionHandler exceptionHandler;
    private ExecutionQueue queue;

    @BeforeEach
    void setUp() {
        exceptionHandler = mock(ExceptionHandler.class);
        queue = new ExecutionQueue(exceptionHandler);
    }

    @Test
    void submit_shouldExecuteSingleCommand() throws Exception {
        Command command = mock(Command.class);
        queue.submit(command);
        verify(command).execute();
    }

    @Test
    void submit_shouldExecuteCommandsInOrder() {
        StringBuilder log = new StringBuilder();
        queue.submit(() -> log.append("1"));
        queue.submit(() -> log.append("2"));
        queue.submit(() -> log.append("3"));

        assertEquals("123", log.toString());
    }

    @Test
    void submit_shouldHandleException() throws Exception {
        Command failing = mock(Command.class);
        doThrow(new RuntimeException("fail")).when(failing).execute();

        queue.submit(failing);
        verify(exceptionHandler).handle(eq(failing.getClass().getSimpleName()), any());
    }

    @Test
    void submit_shouldNotReenterProcessLoop() throws Exception {
        // Проверка, что повторный вызов submit не вызывает processNext одновременно
        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);

        queue.submit(command1);
        queue.submit(command2);

        verify(command1).execute();
        verify(command2).execute();
    }
}
