package org.spacebattle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирует LoggingMiddleware — обёртку для логирования команд.
 */
class LoggingMiddlewareTest {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.clearProperty("log.commands");
    }

    @Test
    void testCommandExecutesWithoutLoggingIfPropertyMissing() throws Exception {
        Command command = mock(Command.class);
        LoggingMiddleware middleware = new LoggingMiddleware();
        Command wrapped = middleware.wrap(command);

        wrapped.execute();

        verify(command).execute();
        assertEquals("", outContent.toString().trim());
    }

    @Test
    void testCommandExecutesWithLoggingIfPropertySet() throws Exception {
        System.setProperty("log.commands", "true");

        Command command = mock(Command.class);
        LoggingMiddleware middleware = new LoggingMiddleware();
        Command wrapped = middleware.wrap(command);

        wrapped.execute();

        verify(command).execute();
        String output = outContent.toString().trim();
        assertTrue(output.contains("[log] → Executing command:"));
    }
}
