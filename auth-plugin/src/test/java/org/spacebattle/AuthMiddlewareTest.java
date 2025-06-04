package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.context.CommandContext;
import org.spacebattle.context.Contextual;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthMiddlewareTest {

    private AccessRegistry accessRegistry;
    private AuthMiddleware middleware;

    @BeforeEach
    void setUp() {
        accessRegistry = mock(AccessRegistry.class);
        middleware = new AuthMiddleware(accessRegistry);
    }

    @Test
    void wrap_shouldExecuteCommand_whenAccessIsAllowed() throws Exception {
        CommandContext context = mock(CommandContext.class);
        when(context.get("userId")).thenReturn(Optional.of("user1"));
        when(context.get("objectId")).thenReturn(Optional.of("obj1"));
        when(accessRegistry.isAllowed("user1", "obj1")).thenReturn(true);

        Command command = mock(Command.class, withSettings().extraInterfaces(Contextual.class));
        Contextual contextual = (Contextual) command;
        when(contextual.getContext()).thenReturn(context);

        Command wrapped = middleware.wrap(command);
        wrapped.execute();

        verify(command).execute();
    }

    @Test
    void wrap_shouldThrow_whenCommandIsNotContextual() {
        Command nonContextualCommand = mock(Command.class);

        Command wrapped = middleware.wrap(nonContextualCommand);

        SecurityException e = assertThrows(SecurityException.class, wrapped::execute);
        assertEquals("Command is not contextual!", e.getMessage());
    }

    @Test
    void wrap_shouldThrow_whenUserIdMissing() {
        CommandContext context = mock(CommandContext.class);
        when(context.get("userId")).thenReturn(Optional.empty());
        when(context.get("objectId")).thenReturn(Optional.of("obj1"));

        Command command = mock(Command.class, withSettings().extraInterfaces(Contextual.class));
        Contextual contextual = (Contextual) command;
        when(contextual.getContext()).thenReturn(context);

        Command wrapped = middleware.wrap(command);

        SecurityException e = assertThrows(SecurityException.class, wrapped::execute);
        assertEquals("Missing userId in context", e.getMessage());
    }

    @Test
    void wrap_shouldThrow_whenObjectIdMissing() {
        CommandContext context = mock(CommandContext.class);
        when(context.get("userId")).thenReturn(Optional.of("user1"));
        when(context.get("objectId")).thenReturn(Optional.empty());

        Command command = mock(Command.class, withSettings().extraInterfaces(Contextual.class));
        Contextual contextual = (Contextual) command;
        when(contextual.getContext()).thenReturn(context);

        Command wrapped = middleware.wrap(command);

        SecurityException e = assertThrows(SecurityException.class, wrapped::execute);
        assertEquals("Missing objectId in context", e.getMessage());
    }

    @Test
    void wrap_shouldThrow_whenAccessDenied() {
        CommandContext context = mock(CommandContext.class);
        when(context.get("userId")).thenReturn(Optional.of("user1"));
        when(context.get("objectId")).thenReturn(Optional.of("obj1"));
        when(accessRegistry.isAllowed("user1", "obj1")).thenReturn(false);

        Command command = mock(Command.class, withSettings().extraInterfaces(Contextual.class));
        Contextual contextual = (Contextual) command;
        when(contextual.getContext()).thenReturn(context);

        Command wrapped = middleware.wrap(command);

        SecurityException e = assertThrows(SecurityException.class, wrapped::execute);
        assertEquals("User user1 has no access to object obj1", e.getMessage());
    }
}
