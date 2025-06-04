package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JsonDSLCommandFactoryTest {

    private JsonDSLCommandFactory factory;
    private IoC ioc;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        factory = new JsonDSLCommandFactory();
        factory.setIoC(ioc);
    }

    @Test
    void create_shouldReturnJsonDSLCommandRunner() {
        IUObject target = mock(IUObject.class);
        CommandDTO dto = new CommandDTO("someId", "dsl", null);

        Command command = factory.create(target, dto);

        assertNotNull(command, "Команда не должна быть null");
        assertTrue(command instanceof JsonDSLCommandRunner, "Команда должна быть экземпляром JsonDSLCommandRunner");
    }

    @Test
    void create_shouldUseInjectedIoC_reflectively() throws Exception {
        IUObject target = mock(IUObject.class);
        CommandDTO dto = new CommandDTO("someId", "dsl", null);

        JsonDSLCommandFactory factory = new JsonDSLCommandFactory();
        factory.setIoC(ioc);

        Command command = factory.create(target, dto);

        assertNotNull(command);
        assertTrue(command instanceof JsonDSLCommandRunner);

        Field iocField = JsonDSLCommandRunner.class.getDeclaredField("container");
        iocField.setAccessible(true);
        IoC injectedIoc = (IoC) iocField.get(command);

        assertSame(ioc, injectedIoc, "IoC контейнер внутри команды должен быть тем же, что внедрён в фабрику");
    }


}
