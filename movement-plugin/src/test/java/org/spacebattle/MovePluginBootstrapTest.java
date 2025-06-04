package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.ioc.IoC;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class MovePluginBootstrapTest {

    private TestIoCContainer ioc;
    private MovePluginBootstrap bootstrap;

    @BeforeEach
    void setup() {
        ioc = new TestIoCContainer();

        // Заглушка для IoC.Register — просто сохраняет зависимости в контейнер
        ioc.register("IoC.Register", args -> {
            String key = (String) args[0];
            @SuppressWarnings("unchecked")
            Function<Object[], Object> supplier = (Function<Object[], Object>) args[1];
            ioc.register(key, supplier);
            return null;
        });

        bootstrap = new MovePluginBootstrap();
        bootstrap.setIoC(ioc);
    }


    @Test
    void testRun_shouldRegisterMoveCommandFactory() {
        bootstrap.run();

        // Проверяем, что зарегистрирован ключ "command:move"
        assertTrue(ioc.contains("command:move"));

        // Проверяем, что возвращается экземпляр MoveCommandFactory
        Object result = ioc.resolve("command:move");
        assertNotNull(result);
        assertInstanceOf(MoveCommandFactory.class, result);
    }

    /**
     * Вспомогательный класс IoC-контейнера для тестов.
     */
    static class TestIoCContainer implements IoC {
        private final Map<String, Function<Object[], Object>> registry = new HashMap<>();

        @Override
        public void register(String key, Function<Object[], Object> supplier) {
            registry.put(key, supplier);
        }

        @Override
        public Object resolve(String key, Object... args) {
            Function<Object[], Object> supplier = registry.get(key);
            if (supplier == null) throw new RuntimeException("Not found: " + key);
            return supplier.apply(args);
        }

        public boolean contains(String key) {
            return registry.containsKey(key);
        }
    }
}
