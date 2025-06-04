package org.spacebattle.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleExceptionRegistryTest {

    private SimpleExceptionRegistry registry;
    private ExceptionHandler testHandler;
    private ExceptionHandler fallbackHandler;

    @BeforeEach
    void setUp() {
        registry = new SimpleExceptionRegistry();
        testHandler = (source, ex) -> System.out.println("Handled: " + ex.getMessage());
        fallbackHandler = (source, ex) -> System.out.println("Fallback: " + ex.getMessage());
    }

    @Test
    void testRegisterAndResolve_existingHandler() {
        registry.register("MyHandler", testHandler);
        ExceptionHandler resolved = registry.resolve("MyHandler");

        assertSame(testHandler, resolved, "Should return the registered handler");
    }

    @Test
    void testResolve_unknownHandler_returnsFallback() {
        ExceptionHandler resolved = registry.resolve("Unknown");

        assertNotNull(resolved, "Fallback should not be null");
        assertTrue(resolved instanceof DefaultExceptionHandler, "Default fallback should be used");
    }

    @Test
    void testSetCustomFallback_returnsCustomFallback() {
        registry.setFallback(fallbackHandler);
        ExceptionHandler resolved = registry.resolve("nonexistent");

        assertSame(fallbackHandler, resolved, "Should return the custom fallback handler");
    }
}
