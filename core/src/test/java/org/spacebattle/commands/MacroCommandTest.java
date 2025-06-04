package org.spacebattle.commands;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MacroCommandTest {

    @Test
    void testAllCommandsExecutedInOrder() throws Exception {
        List<String> log = new ArrayList<>();

        Command first = () -> log.add("first");
        Command second = () -> log.add("second");
        Command third = () -> log.add("third");

        Command macro = new MacroCommand(List.of(first, second, third));
        macro.execute();

        assertEquals(List.of("first", "second", "third"), log);
    }

    @Test
    void testMacroCommandThrowsIfOneFails() {
        Command ok1 = () -> {};
        Command fail = () -> { throw new RuntimeException("Failure"); };
        Command ok2 = () -> fail("Should not be reached");

        Command macro = new MacroCommand(List.of(ok1, fail, ok2));

        Exception ex = assertThrows(RuntimeException.class, macro::execute);
        assertEquals("Failure", ex.getMessage());
    }
}
