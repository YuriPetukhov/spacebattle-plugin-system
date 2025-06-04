package org.spacebattle.exception;

import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class BasicFallbackExceptionHandlerTest {

    @Test
    void handle_shouldPrintToStandardError() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            ExceptionHandler handler = new BasicFallbackExceptionHandler();
            Exception ex = new IllegalStateException("test message");

            handler.handle("test-source", ex);

            String output = errContent.toString();
            assertTrue(output.contains("test-source"));
            assertTrue(output.contains("IllegalStateException"));
            assertTrue(output.contains("test message"));

        } finally {
            System.setErr(originalErr);
        }
    }
}
