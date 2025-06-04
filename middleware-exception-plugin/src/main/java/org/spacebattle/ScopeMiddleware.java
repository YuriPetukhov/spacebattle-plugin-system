package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.middleware.Middleware;

import java.util.HashMap;
import java.util.Map;

/**
 * Middleware, создающее изолированный Scope (область данных) на время выполнения команды.
 * <p>
 * Scope хранится в {@link ThreadLocal}, что делает его безопасным для многопоточности.
 * Используется для хранения временного состояния, которое должно быть видно только во время исполнения одной команды.
 */
public class ScopeMiddleware implements Middleware {

    private static final ThreadLocal<Scope> context = ThreadLocal.withInitial(Scope::new);

    /**
     * Возвращает текущий {@link Scope}, связанный с потоком.
     *
     * @return объект {@link Scope}, ассоциированный с текущим потоком
     */
    public static Scope currentScope() {
        return context.get();
    }

    /**
     * Оборачивает команду, создавая новый scope на время её выполнения.
     * Scope автоматически очищается после завершения команды.
     *
     * @param next команда, которую нужно выполнить
     * @return новая команда с управлением scope
     */
    @Override
    public Command wrap(Command next) {
        return () -> {
            try {
                context.set(new Scope());
                next.execute();
            } finally {
                context.remove();
            }
        };
    }

    /**
     * Представляет собой хранилище ключ-значение в пределах одного потока.
     */
    public static class Scope {
        private final Map<String, Object> data = new HashMap<>();

        /**
         * Сохраняет значение по ключу.
         *
         * @param key   ключ
         * @param value значение
         */
        public void set(String key, Object value) {
            data.put(key, value);
        }

        /**
         * Возвращает значение по ключу или null, если его нет.
         *
         * @param key ключ
         * @return значение или null
         */
        public Object get(String key) {
            return data.get(key);
        }

        /**
         * Удаляет все значения в текущем scope.
         */
        public void clear() {
            data.clear();
        }
    }
}
