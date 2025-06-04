package org.spacebattle.plugins.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.ioc.IoC;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class CommandComponentTest {

    private TestIoCContainer ioc;
    private CommandComponent component;

    @BeforeEach
    void setup() {
        ioc = new TestIoCContainer();
        component = new CommandComponent(ioc);
    }

    @Test
    void testRegistersCommandFactory() throws Exception {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "commands", List.of(
                                Map.of(
                                        "name", "test-command",
                                        "handler", MockCommandFactory.class.getName(),
                                        "type", "factory"
                                )
                        )
                )
        );

        component.apply(getClass().getClassLoader(), pluginSpec);

        Function<Object[], Object> factorySupplier = ioc.get("command:test-command");
        assertNotNull(factorySupplier);
        Object factory = factorySupplier.apply(new Object[0]);
        assertTrue(factory instanceof MockCommandFactory);
    }

    // Вспомогательный мок IoC
    static class TestIoCContainer implements IoC {
        private final Map<String, Function<Object[], Object>> storage = new java.util.HashMap<>();

        @Override
        public Object resolve(String key, Object... args) {
            if (key.equals("IoC.Register") && args.length == 2) {
                String regKey = (String) args[0];
                @SuppressWarnings("unchecked")
                Function<Object[], Object> supplier = (Function<Object[], Object>) args[1];
                register(regKey, supplier); // перехватываем регистрацию
                return null;
            }

            if (!storage.containsKey(key)) throw new RuntimeException("Not found: " + key);
            return storage.get(key).apply(args);
        }

        @Override
        public boolean contains(String key) {
            return false;
        }

        public void register(String key, Function<Object[], Object> supplier) {
            storage.put(key, supplier);
        }

        public Function<Object[], Object> get(String key) {
            return storage.get(key);
        }
    }

    // Фейковая фабрика команды
    public static class MockCommandFactory {
        public void setIoC(IoC ioc) {
            System.out.println("IoC injected into MockCommandFactory");
        }
    }
}
