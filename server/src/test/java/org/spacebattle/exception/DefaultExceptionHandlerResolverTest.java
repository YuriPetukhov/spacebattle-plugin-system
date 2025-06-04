package org.spacebattle.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultExceptionHandlerResolverTest {

    private ExceptionHandler fallback;
    private ExceptionHandler handlerA;
    private DefaultExceptionHandlerResolver resolver;

    @BeforeEach
    void setUp() {
        fallback = mock(ExceptionHandler.class);
        handlerA = mock(ExceptionHandler.class);
        resolver = new DefaultExceptionHandlerResolver(fallback);
    }

    @Test
    void resolve_shouldReturnFallback_whenHandlerNotRegistered() {
        ExceptionHandler resolved = resolver.resolve("unknown");

        assertSame(fallback, resolved);
    }

    @Test
    void resolve_shouldReturnRegisteredHandler_whenHandlerExists() {
        resolver.register("commandA", handlerA);

        ExceptionHandler resolved = resolver.resolve("commandA");

        assertSame(handlerA, resolved);
    }

    @Test
    void register_shouldOverridePreviousHandler() {
        ExceptionHandler handler1 = mock(ExceptionHandler.class);
        ExceptionHandler handler2 = mock(ExceptionHandler.class);

        resolver.register("conflict", handler1);
        resolver.register("conflict", handler2);

        assertSame(handler2, resolver.resolve("conflict"));
    }
}
