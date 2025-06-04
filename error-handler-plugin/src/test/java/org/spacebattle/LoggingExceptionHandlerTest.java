package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingExceptionHandlerTest {

    @Test
    void testLogsFormattedMessageToStderr() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            ExceptionHandler handler = new LoggingExceptionHandler();
            handler.handle("TestSource", new IllegalArgumentException("Invalid argument"));

            String output = errContent.toString().trim();
            assertTrue(output.contains("[EXCEPTION] Command: TestSource"));
            assertTrue(output.contains("IllegalArgumentException"));
            assertTrue(output.contains("Invalid argument"));
        } finally {
            System.setErr(originalErr);
        }
    }
}
