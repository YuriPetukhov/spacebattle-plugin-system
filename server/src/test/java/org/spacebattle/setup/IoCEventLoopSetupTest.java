package org.spacebattle.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.execution.ExecutionQueue;
import org.spacebattle.ioc.IoCContainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IoCEventLoopSetupTest {

    private IoCContainer ioc;
    private ExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        ioc = new IoCContainer();
        exceptionHandler = mock(ExceptionHandler.class);
        ioc.register("exception-handler", args -> exceptionHandler);
    }

    @Test
    void testSetupRegistersExecutionQueueWhenLoopIsExecution() {
        System.setProperty("loop", "execution");

        IoCEventLoopSetup.setup(ioc);

        Object resolved = ioc.resolve("event-loop");
        assertNotNull(resolved);
        assertTrue(resolved instanceof ExecutionQueue);
    }

    @Test
    void testSetupRegistersEventLoopWhenLoopIsNotExecution() {
        System.clearProperty("loop"); // default should be event

        IoCEventLoopSetup.setup(ioc);

        Object resolved = ioc.resolve("event-loop");
        assertNotNull(resolved);
        assertTrue(resolved instanceof EventLoop);
    }

    @Test
    void testEventLoopIsStartedOnSetup() throws Exception {
        System.clearProperty("loop");

        IoCEventLoopSetup.setup(ioc);

        EventLoop loop = (EventLoop) ioc.resolve("event-loop");
        assertNotNull(loop);

        // Мокаем команду
        Command cmd = mock(Command.class);
        loop.submit(cmd);

        // Дадим немного времени на выполнение
        Thread.sleep(100);

        verify(cmd, atLeastOnce()).execute(); // проверка, что команда выполнена
    }

}