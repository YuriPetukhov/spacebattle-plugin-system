package org.spacebattle.execution;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventLoopTest {

    private EventLoop eventLoop;

    @AfterEach
    void tearDown() throws InterruptedException {
        if (eventLoop != null) {
            eventLoop.softStop();
        }
    }

    @Test
    void shouldExecuteCommand() throws Exception {
        Command command = mock(Command.class);
        ExceptionHandler handler = mock(ExceptionHandler.class);

        eventLoop = new EventLoop(1, handler);
        eventLoop.start();

        eventLoop.submit(command);
        Thread.sleep(50);

        verify(command).execute();
        verifyNoInteractions(handler);
    }

    @Test
    void shouldHandleExceptionFromCommand() throws Exception {
        Command command = mock(Command.class);
        doThrow(new RuntimeException("fail")).when(command).execute();

        ExceptionHandler handler = mock(ExceptionHandler.class);
        eventLoop = new EventLoop(1, handler);
        eventLoop.start();

        eventLoop.submit(command);
        Thread.sleep(50);

        verify(command).execute();
        verify(handler).handle(eq("EventLoop"), any());
    }

    @Test
    void shouldRunIdleBehaviourWhenQueueIsEmpty() throws InterruptedException {
        ExceptionHandler handler = mock(ExceptionHandler.class);
        AtomicBoolean called = new AtomicBoolean(false);

        Runnable idle = () -> called.set(true);

        eventLoop = new EventLoop(1, handler);
        eventLoop.setBehaviour(idle);
        eventLoop.start();

        eventLoop.submit(() -> {});

        Thread.sleep(100);
        assertTrue(called.get());
    }


    @Test
    void shouldStopImmediately() throws InterruptedException {
        ExceptionHandler handler = mock(ExceptionHandler.class);
        eventLoop = new EventLoop(1, handler);
        eventLoop.start();

        eventLoop.stop();
        assertTrue(eventLoop.getWorkers().isShutdown());
    }

    @Test
    void shouldShutdownSoftly() throws InterruptedException {
        ExceptionHandler handler = mock(ExceptionHandler.class);
        eventLoop = new EventLoop(1, handler);
        eventLoop.start();

        eventLoop.softStop();
        assertTrue(eventLoop.getWorkers().isShutdown());
    }
}
