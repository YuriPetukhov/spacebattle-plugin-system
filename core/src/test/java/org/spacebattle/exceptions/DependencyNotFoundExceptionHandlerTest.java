package org.spacebattle.exceptions;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.spi.LoggingEventBuilder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Простой smoke-тест для DependencyNotFoundExceptionHandler.
 * Проверяет, что handle не выбрасывает исключение.
 */
class DependencyNotFoundExceptionHandlerTest {

    @Test
    void testHandle_doesNotThrowException() {
        DependencyNotFoundExceptionHandler handler = new DependencyNotFoundExceptionHandler();

        Exception ex = new DependencyNotFoundException("serviceX");

        assertDoesNotThrow(() -> handler.handle("MyClass", ex));
    }

    @Test
    void testHandle_acceptsAnyException() {
        DependencyNotFoundExceptionHandler handler = new DependencyNotFoundExceptionHandler();

        RuntimeException ex = new RuntimeException("Generic error");

        assertDoesNotThrow(() -> handler.handle("AnyClass", ex));
    }
}
