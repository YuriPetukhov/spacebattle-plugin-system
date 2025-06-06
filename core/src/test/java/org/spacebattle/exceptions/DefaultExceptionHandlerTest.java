package org.spacebattle.exceptions;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class DefaultExceptionHandlerTest {

    @Test
    void testHandleOutputsToStderr() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            ExceptionHandler handler = new DefaultExceptionHandler(new Object[0]);
            RuntimeException ex = new RuntimeException("Something went wrong");

            handler.handle("TestClass", ex);

            String output = errContent.toString();
            assertTrue(output.contains("[EXCEPTION] TestClass: Something went wrong"));
            assertTrue(output.contains("java.lang.RuntimeException"));
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void testConstructorDoesNotFail() {
        assertDoesNotThrow(() -> new DefaultExceptionHandler(new Object[]{"any", 123}));
    }
}
