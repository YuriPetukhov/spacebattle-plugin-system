package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JsonDSLCommandRunnerTest {

    private IoC ioc;
    private EventLoop eventLoop;
    private CommandFactory moveFactory;
    private CommandFactory rotateFactory;

    private IUObject ship1;
    private IUObject ship2;
    private Command moveCommand;
    private Command rotateCommand;

    private File tempJson;

    @BeforeEach
    void setUp() throws Exception {
        ioc = mock(IoC.class);
        eventLoop = mock(EventLoop.class);
        moveFactory = mock(CommandFactory.class);
        rotateFactory = mock(CommandFactory.class);

        ship1 = mock(IUObject.class);
        ship2 = mock(IUObject.class);

        moveCommand = mock(Command.class);
        rotateCommand = mock(Command.class);

        when(ioc.resolve(eq("event-loop"))).thenReturn(eventLoop);
        when(ioc.resolve(eq("command:move"))).thenReturn(moveFactory);
        when(ioc.resolve(eq("command:rotate"))).thenReturn(rotateFactory);

        when(moveFactory.create(any(), any())).thenReturn(moveCommand);
        when(rotateFactory.create(any(), any())).thenReturn(rotateCommand);

        List<CommandDTO> commands = List.of(
                new CommandDTO("ship-1", "move", Map.of("dx", 1, "dy", 2)),
                new CommandDTO("ship-2", "rotate", Map.of("angle", 90))
        );
        ObjectMapper mapper = new ObjectMapper();
        tempJson = new File("commands.json");
        mapper.writeValue(tempJson, commands);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempJson.toPath());
    }

    @Test
    void execute_shouldRunCommandsFromFile() throws Exception {
        when(ioc.resolve(eq("object:ship-1"))).thenThrow(new RuntimeException("not found"));
        when(ioc.resolve(eq("object:ship-2"))).thenThrow(new RuntimeException("not found"));

        doAnswer(invocation -> {
            Function<Object[], Object> func = invocation.getArgument(2);
            return func.apply(null);
        }).when(ioc).resolve(eq("IoC.Register"), eq("object:ship-1"), any(Function.class));
        doAnswer(invocation -> {
            Function<Object[], Object> func = invocation.getArgument(2);
            return func.apply(null);
        }).when(ioc).resolve(eq("IoC.Register"), eq("object:ship-2"), any(Function.class));

        JsonDSLCommandRunner runner = new JsonDSLCommandRunner(ioc);
        runner.execute();

        verify(eventLoop).submit(moveCommand);
        verify(eventLoop).submit(rotateCommand);
    }

    @Test
    void execute_shouldSkipIfFileNotExists() throws Exception {
        Files.deleteIfExists(tempJson.toPath());
        JsonDSLCommandRunner runner = new JsonDSLCommandRunner(ioc);
        runner.execute();

        verify(eventLoop, never()).submit(any());
    }
}
