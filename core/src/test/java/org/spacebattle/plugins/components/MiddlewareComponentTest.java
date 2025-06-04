package org.spacebattle.plugins.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.middleware.Middleware;
import org.spacebattle.middleware.MiddlewareRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MiddlewareComponentTest {

    private TestMiddlewareRegistry registry;
    private MiddlewareComponent component;

    @BeforeEach
    void setup() {
        registry = new TestMiddlewareRegistry();
        component = new MiddlewareComponent(registry);
    }

    @Test
    void testRegistersMiddleware() throws Exception {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "middleware", List.of(
                                Map.of("class", TestMiddleware.class.getName())
                        )
                )
        );

        component.apply(getClass().getClassLoader(), pluginSpec);

        assertEquals(1, registry.count);
        assertTrue(registry.lastRegistered instanceof TestMiddleware);
    }

    public static class TestMiddleware implements Middleware {
        @Override
        public Command wrap(Command command) {
            return command;
        }
    }

    static class TestMiddlewareRegistry extends MiddlewareRegistry {
        public Middleware lastRegistered;
        public int count = 0;

        @Override
        public void register(Middleware middleware) {
            count++;
            lastRegistered = middleware;
        }
    }
}
