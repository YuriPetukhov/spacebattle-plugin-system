package org.spacebattle.context;

import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;

import static org.junit.jupiter.api.Assertions.*;

class ContextualCommandTest {

    @Test
    void testGetContext_returnsExpectedContext() {
        CommandContext context = new CommandContext();
        Command inner = () -> {}; // пустая команда
        ContextualCommand contextual = new ContextualCommand(inner, context);

        assertSame(context, contextual.getContext());
    }

    @Test
    void testExecute_delegatesToInnerCommand() throws Exception {
        final boolean[] executed = {false};
        Command inner = () -> executed[0] = true;
        ContextualCommand contextual = new ContextualCommand(inner, new CommandContext());

        contextual.execute();

        assertTrue(executed[0], "Вложенная команда должна быть выполнена");
    }

    @Test
    void testExecute_throwsExceptionFromInnerCommand() {
        Command failingCommand = () -> { throw new IllegalStateException("Ошибка"); };
        ContextualCommand contextual = new ContextualCommand(failingCommand, new CommandContext());

        Exception ex = assertThrows(IllegalStateException.class, contextual::execute);
        assertEquals("Ошибка", ex.getMessage());
    }

    @Test
    void testConstructor_storesInnerCommand() {
        Command inner = () -> {};
        ContextualCommand contextual = new ContextualCommand(inner, new CommandContext());

        assertSame(inner, contextual.getInner());
    }
}
