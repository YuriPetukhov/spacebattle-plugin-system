package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.ioc.IoC;
import org.spacebattle.repository.ObjectRepository;
import org.spacebattle.uobject.IUObject;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CommandProcessorTest {

    private IoC ioc;
    private CommandProcessor processor;

    @BeforeEach
    void setUp() {
        ioc = mock(IoC.class);
        processor = new CommandProcessor(ioc);
    }

    @Test
    void process_shouldDispatchCommandAndReturnUpdatedProperties() throws Exception {
        CommandDTO dto = new CommandDTO("ship-1", "move", Map.of("dx", 1, "dy", 2));

        IUObject object = mock(IUObject.class);
        when(object.getAllProperties()).thenReturn(Map.of("x", 1, "y", 2));

        ObjectRepository repository = mock(ObjectRepository.class);
        when(repository.findById("ship-1")).thenReturn(Optional.of(object));
        when(ioc.resolve("object-repository")).thenReturn(repository);

        CommandFactory factory = mock(CommandFactory.class);
        Command command = mock(Command.class);
        when(factory.create(object, dto)).thenReturn(command);
        when(ioc.resolve("command:move")).thenReturn(factory);

        Function<Command, Void> dispatcher = mock(Function.class);
        when(dispatcher.apply(command)).thenReturn(null);
        when(ioc.resolve("command-dispatcher")).thenReturn(dispatcher);

        Map<String, Object> result = processor.process(dto);

        assertEquals("ok", result.get("status"));
        assertEquals("move", result.get("action"));
        assertEquals("ship-1", result.get("objectId"));
        assertEquals(Map.of("x", 1, "y", 2), result.get("properties"));
    }

    @Test
    void process_shouldThrowIfObjectNotFound() throws Exception {
        CommandDTO dto = new CommandDTO("missing", "move", Map.of());
        ObjectRepository repository = mock(ObjectRepository.class);
        when(repository.findById("missing")).thenReturn(Optional.empty());
        when(ioc.resolve("object-repository")).thenReturn(repository);

        Exception e = assertThrows(ClientInputException.class, () -> processor.process(dto));
        assertTrue(e.getMessage().contains("Объект не найден"));
    }
}
