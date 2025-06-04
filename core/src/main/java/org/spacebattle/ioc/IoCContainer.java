package org.spacebattle.ioc;

import org.spacebattle.exceptions.DependencyNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Простейшая реализация IoC-контейнера с поддержкой глобального
 * и скоуп-зависимого (локального) хранения зависимостей.
 */
public class IoCContainer implements IoC {

    private final Map<String, Function<Object[], Object>> globalRegistry = new HashMap<>();
    private final ThreadLocal<Map<String, Function<Object[], Object>>> scopedRegistry =
            ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<String> currentScope = ThreadLocal.withInitial(() -> "global");

    /**
     * Регистрирует глобальную зависимость по ключу.
     * Она будет доступна из любого скоупа.
     */
    @Override
    public void register(String key, Function<Object[], Object> factory) {
        globalRegistry.put(key, factory);
    }

    /**
     * Регистрирует зависимость, видимую только в текущем потоке (скоупе).
     */

    public void registerScoped(String key, Function<Object[], Object> factory) {
        scopedRegistry.get().put(key, factory);
    }

    /**
     * Создаёт новый скоуп (локальное пространство зависимостей).
     * Сбросит текущие локальные зависимости.
     */
    public void newScope(String scopeId) {
        scopedRegistry.set(new HashMap<>());
        currentScope.set(scopeId);
    }

    /**
     * Возвращает текущий идентификатор скоупа.
     */
    public String getCurrentScope() {
        return currentScope.get();
    }

    /**
     * Разрешает зависимость по ключу.
     * Сначала ищет в текущем скоупе, затем в глобальном пространстве.
     * Передаёт аргументы в фабрику.
     */
    @Override
    public Object resolve(String key, Object... args) {
        if ("IoC.Register".equals(key)) {
            System.out.printf("IoC.Register called: key = %s, supplier = %s%n", args[0], args[1]);
        }

        Function<Object[], Object> factory = scopedRegistry.get().getOrDefault(key, globalRegistry.get(key));
        if (factory == null) {
            throw new DependencyNotFoundException(key);
        }
        return factory.apply(args);
    }

    /**
     * Проверяет, зарегистрирована ли зависимость в текущем скоупе или глобально.
     */
    @Override
    public boolean contains(String key) {
        return scopedRegistry.get().containsKey(key) || globalRegistry.containsKey(key);
    }

}
