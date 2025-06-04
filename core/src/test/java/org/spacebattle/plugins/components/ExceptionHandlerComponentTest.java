package org.spacebattle.plugins.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandlerResolver;
import org.spacebattle.ioc.IoCContainer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionHandlerComponentTest {

    private TestIoCContainer ioc;
    private ExceptionHandlerComponent component;

    @BeforeEach
    void setup() {
        ioc = new TestIoCContainer();
        component = new ExceptionHandlerComponent(ioc);
    }

    @Test
    void testRegistersCustomHandlerFromExceptionHandlersKey() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "exceptionHandlers", List.of(
                                Map.of("class", TestHandler.class.getName())
                        )
                )
        );

        component.apply(getClass().getClassLoader(), pluginSpec);

        assertHandlerIs(TestHandler.class);
    }

    @Test
    void testRegistersCustomHandlerFromLegacyExceptionsKey() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "exceptions", List.of(
                                Map.of("class", TestHandler.class.getName())
                        )
                )
        );

        component.apply(getClass().getClassLoader(), pluginSpec);

        assertHandlerIs(TestHandler.class);
    }

    @Test
    void testFallbackToDefaultHandlerWhenNoSpec() {
        Map<String, Object> pluginSpec = Map.of("plugin", Map.of());

        component.apply(getClass().getClassLoader(), pluginSpec);

        assertNotNull(ioc.get("exception-handler"));
        Object handler = ioc.get("exception-handler").apply(new Object[0]);
        assertEquals("org.spacebattle.exceptions.DefaultExceptionHandler", handler.getClass().getName());
    }

    private void assertHandlerIs(Class<?> expectedHandlerClass) {
        assertTrue(ioc.contains("exception-resolver"));
        ExceptionHandlerResolver resolver = (ExceptionHandlerResolver) ioc.get("exception-resolver").apply(new Object[0]);
        ExceptionHandler handler = resolver.resolve("test");
        assertNotNull(handler);
        assertEquals(expectedHandlerClass, handler.getClass());
    }

    public static class TestHandler implements ExceptionHandler {
        @Override
        public void handle(String source, Exception e) {
            System.out.println("Handled: " + source);
        }
    }

    static class TestIoCContainer extends IoCContainer {
        private final Map<String, Function<Object[], Object>> storage = new java.util.HashMap<>();

        @Override
        public void register(String key, Function<Object[], Object> supplier) {
            storage.put(key, supplier);
        }

        @Override
        public Object resolve(String key, Object... args) {
            if (!storage.containsKey(key)) throw new RuntimeException("Not found: " + key);
            return storage.get(key).apply(args);
        }

        public boolean contains(String key) {
            return storage.containsKey(key);
        }

        public Function<Object[], Object> get(String key) {
            return storage.get(key);
        }
    }
}
