package org.spacebattle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientInputExceptionTest {

    @Test
    void constructor_shouldStoreMessage() {
        String message = "Invalid input";
        ClientInputException ex = new ClientInputException(message);

        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructor_shouldStoreMessageAndCause() {
        String message = "Invalid format";
        Throwable cause = new IllegalArgumentException("Bad value");
        ClientInputException ex = new ClientInputException(message, cause);

        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
