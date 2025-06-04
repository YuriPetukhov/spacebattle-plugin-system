package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;

import static org.junit.jupiter.api.Assertions.*;

class ScopeMiddlewareTest {

    @Test
    void testScopeIsIsolatedPerExecution() throws Exception {
        ScopeMiddleware middleware = new ScopeMiddleware();

        // Команда 1: устанавливает значение в scope
        Command command1 = middleware.wrap(() -> {
            ScopeMiddleware.currentScope().set("key", "value1");
            assertEquals("value1", ScopeMiddleware.currentScope().get("key"));
        });

        // Команда 2: ничего не устанавливает, проверяет отсутствие ключа
        Command command2 = middleware.wrap(() -> {
            assertNull(ScopeMiddleware.currentScope().get("key"));
        });

        // Выполнение
        command1.execute();
        command2.execute(); // Убедимся, что значение не "просочилось"
    }

    @Test
    void testScopeRemovedAfterExecution() throws Exception {
        ScopeMiddleware middleware = new ScopeMiddleware();

        Command command = middleware.wrap(() -> {
            ScopeMiddleware.currentScope().set("x", 42);
            assertEquals(42, ScopeMiddleware.currentScope().get("x"));
        });

        command.execute();

        // После выполнения контекст очищен — создаётся новый Scope
        assertNull(ScopeMiddleware.currentScope().get("x"));
    }

    @Test
    void testScopeClearMethod() {
        ScopeMiddleware.Scope scope = new ScopeMiddleware.Scope();
        scope.set("a", 1);
        scope.set("b", 2);

        assertEquals(1, scope.get("a"));
        assertEquals(2, scope.get("b"));

        scope.clear();

        assertNull(scope.get("a"));
        assertNull(scope.get("b"));
    }
}
