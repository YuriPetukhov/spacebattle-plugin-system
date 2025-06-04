package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.EventLoopCommandRegistrar;
import org.spacebattle.HardStopCommand;
import org.spacebattle.SoftStopCommand;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoCContainer;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventLoopCommandRegistrarTest {

    private IoCContainer ioc;
    private EventLoop mockLoop;

    @BeforeEach
    void setUp() {
        ioc = new IoCContainer();

        ioc.register("IoC.Register", args -> {
            String key = (String) args[0];
            @SuppressWarnings("unchecked")
            Function<Object[], Object> factory = (Function<Object[], Object>) args[1];
            ioc.register(key, factory);
            return null;
        });

        ExceptionHandler handler = mock(ExceptionHandler.class);
        mockLoop = new EventLoop(1, handler);
        ioc.register("eventLoop", args -> mockLoop);
    }

    @Test
    void testRegisterCommands() {
        EventLoopCommandRegistrar registrar = new EventLoopCommandRegistrar();
        registrar.register(ioc);

        Object hard = ioc.resolve("command:hardStop");
        Object soft = ioc.resolve("command:softStop");

        assertNotNull(hard);
        assertNotNull(soft);
        assertTrue(hard instanceof HardStopCommand);
        assertTrue(soft instanceof SoftStopCommand);
    }
}
