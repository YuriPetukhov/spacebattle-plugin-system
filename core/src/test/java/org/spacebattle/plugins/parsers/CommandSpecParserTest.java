package org.spacebattle.plugins.parsers;

import org.junit.jupiter.api.Test;
import org.spacebattle.plugins.parsers.models.CommandSpec;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandSpecParserTest {

    private final CommandSpecParser parser = new CommandSpecParser();

    @Test
    void testParseSingleCommand() {
        Map<String, Object> spec = Map.of(
                "plugin", Map.of(
                        "commands", List.of(
                                Map.of(
                                        "name", "move",
                                        "handler", "org.spacebattle.commands.MoveCommand",
                                        "type", "command"
                                )
                        )
                )
        );

        List<CommandSpec> result = parser.parse(spec);

        assertEquals(1, result.size());
        CommandSpec cmd = result.get(0);
        assertEquals("move", cmd.name());
        assertEquals("org.spacebattle.commands.MoveCommand", cmd.className());
        assertEquals("command", cmd.type());
    }

    @Test
    void testParseWithMissingType_usesDefault() {
        Map<String, Object> spec = Map.of(
                "plugin", Map.of(
                        "commands", List.of(
                                Map.of(
                                        "name", "rotate",
                                        "handler", "org.spacebattle.commands.RotateCommand"
                                )
                        )
                )
        );

        List<CommandSpec> result = parser.parse(spec);

        assertEquals(1, result.size());
        CommandSpec cmd = result.get(0);
        assertEquals("rotate", cmd.name());
        assertEquals("org.spacebattle.commands.RotateCommand", cmd.className());
        assertEquals("command", cmd.type()); // default
    }

    @Test
    void testParseEmptyCommands() {
        Map<String, Object> spec = Map.of("plugin", Map.of("commands", List.of()));
        List<CommandSpec> result = parser.parse(spec);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseNoPluginSection_returnsEmpty() {
        Map<String, Object> spec = Map.of(); // no "plugin"
        List<CommandSpec> result = parser.parse(spec);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseNoCommandsSection_returnsEmpty() {
        Map<String, Object> spec = Map.of("plugin", Map.of("middleware", List.of()));
        List<CommandSpec> result = parser.parse(spec);
        assertTrue(result.isEmpty());
    }
}
