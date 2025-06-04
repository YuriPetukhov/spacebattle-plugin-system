package org.spacebattle.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class CentralizedExceptionDispatcherTest {

    private CentralizedExceptionDispatcher dispatcher;
    private AtomicBoolean handled;
    private StringBuilder log;

    @BeforeEach
    void setup() {
        handled = new AtomicBoolean(false);
        log = new StringBuilder();
        dispatcher = new CentralizedExceptionDispatcher((source, e) -> log.append("default:" + e.getClass().getSimpleName()));
    }

    @Test
    void testExactMatchHandlerIsUsed() {
        dispatcher.registerHandler(IllegalArgumentException.class, (source, e) -> handled.set(true));

        dispatcher.handle("test", new IllegalArgumentException("Invalid"));

        assertTrue(handled.get());
    }

    @Test
    void testSuperclassMatchIsUsedIfExactNotFound() {
        dispatcher.registerHandler(RuntimeException.class, (source, e) -> handled.set(true));

        dispatcher.handle("test", new IllegalArgumentException("Invalid"));

        assertTrue(handled.get());
    }

    @Test
    void testDefaultHandlerIsUsedWhenNoMatch() {
        dispatcher.handle("source", new Exception("something"));

        assertTrue(log.toString().startsWith("default:Exception"));
    }

    @Test
    void testSupportsReturnsTrueForRegisteredClass() {
        dispatcher.registerHandler(IllegalArgumentException.class, (source, e) -> {});

        assertTrue(dispatcher.supports(IllegalArgumentException.class));
        assertFalse(dispatcher.supports(NullPointerException.class));
    }

    @Test
    void testHandlerOrderPrefersExactOverSuperclass() {
        AtomicBoolean exactUsed = new AtomicBoolean(false);
        AtomicBoolean superUsed = new AtomicBoolean(false);

        dispatcher.registerHandler(RuntimeException.class, (source, e) -> superUsed.set(true));
        dispatcher.registerHandler(IllegalArgumentException.class, (source, e) -> exactUsed.set(true));

        dispatcher.handle("test", new IllegalArgumentException());

        assertTrue(exactUsed.get());
        assertFalse(superUsed.get());
    }
}
