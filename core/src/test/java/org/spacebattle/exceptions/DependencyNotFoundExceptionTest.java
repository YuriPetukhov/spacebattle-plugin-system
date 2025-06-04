package org.spacebattle.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DependencyNotFoundExceptionTest {

    @Test
    void testMessageIsSetCorrectly() {
        String key = "some-service";
        DependencyNotFoundException ex = new DependencyNotFoundException(key);

        assertEquals("Dependency not found: some-service", ex.getMessage());
    }

    @Test
    void testIsInstanceOfRuntimeException() {
        DependencyNotFoundException ex = new DependencyNotFoundException("test");

        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testToStringContainsKey() {
        DependencyNotFoundException ex = new DependencyNotFoundException("abc");
        assertTrue(ex.toString().contains("abc"));
    }
}
