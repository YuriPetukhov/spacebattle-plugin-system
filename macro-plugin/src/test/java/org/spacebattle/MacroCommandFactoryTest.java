package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.commands.MacroCommand;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MacroCommandFactoryTest {

    private IoC ioc;
    private IUObject target;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        target = mock(IUObject.class);
    }

    @Test
    void testCreateMacroCommandSuccessfully() {
        Command moveCommand = mock(Command.class);
        Command rotateCommand = mock(Command.class);

        CommandFactory moveFactory = mock(CommandFactory.class);
        CommandFactory rotateFactory = mock(CommandFactory.class);

        when(ioc.resolve("command:move")).thenReturn(moveFactory);
        when(ioc.resolve("command:rotate")).thenReturn(rotateFactory);

        when(moveFactory.create(eq(target), any())).thenReturn(moveCommand);
        when(rotateFactory.create(eq(target), any())).thenReturn(rotateCommand);

        Map<String, Object> macroParams = Map.of(
                "commands", List.of(
                        Map.of("action", "move", "params", Map.of("dx", 1, "dy", 2)),
                        Map.of("action", "rotate", "params", Map.of("angle", 90))
                )
        );

        CommandDTO macroDto = new CommandDTO("macro-id", "macro", macroParams);
        MacroCommandFactory factory = new MacroCommandFactory(ioc);

        Command command = factory.create(target, macroDto);

        assertNotNull(command);
        assertTrue(command instanceof MacroCommand);
    }

    @Test
    void testCreateMacroCommandWithInvalidSubcommandThrows() {
        when(ioc.resolve("command:invalid")).thenReturn("not-a-factory");

        Map<String, Object> macroParams = Map.of(
                "commands", List.of(
                        Map.of("action", "invalid", "params", Map.of())
                )
        );

        CommandDTO macroDto = new CommandDTO("macro-id", "macro", macroParams);
        MacroCommandFactory factory = new MacroCommandFactory(ioc);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            factory.create(target, macroDto);
        });

        assertTrue(ex.getMessage().contains("Invalid command in macro"));
    }

    @Test
    void testGetActionNameReturnsMacro() {
        MacroCommandFactory factory = new MacroCommandFactory();
        assertEquals("macro", factory.getActionName());
    }

    @Test
    void testSetIoC() {
        MacroCommandFactory factory = new MacroCommandFactory();

        assertDoesNotThrow(() -> factory.setIoC(ioc));
    }

}
