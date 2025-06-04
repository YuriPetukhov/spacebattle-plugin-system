package org.spacebattle.ioc;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class IoCContainerTest {

    @Test
    void testGlobalRegistrationAndResolve() {
        IoCContainer ioc = new IoCContainer();

        ioc.register("test:hello", args -> "world");

        Object result = ioc.resolve("test:hello");
        assertEquals("world", result);
    }

    @Test
    void testScopedOverridesGlobal() {
        IoCContainer ioc = new IoCContainer();

        ioc.register("value", args -> "global");
        ioc.newScope("test-scope");
        ioc.registerScoped("value", args -> "scoped");

        assertEquals("scoped", ioc.resolve("value"));
    }

    @Test
    void testNewScopeResetsScopedRegistry() {
        IoCContainer ioc = new IoCContainer();

        ioc.newScope("scope1");
        ioc.registerScoped("x", args -> "a");

        ioc.newScope("scope2");
        assertThrows(RuntimeException.class, () -> ioc.resolve("x"));
    }

    @Test
    void testCurrentScopeId() {
        IoCContainer ioc = new IoCContainer();

        ioc.newScope("alpha");
        assertEquals("alpha", ioc.getCurrentScope());
    }

    @Test
    void testRegisterCommandPattern() {
        IoCContainer ioc = new IoCContainer();

        ioc.register("IoC.Register", params -> {
            String key = (String) params[0];
            @SuppressWarnings("unchecked")
            Function<Object[], Object> supplier = (Function<Object[], Object>) params[1];
            ioc.register(key, supplier);
            return null;
        });

        ioc.resolve("IoC.Register", "custom:test", (Function<Object[], Object>) args -> "ok");

        assertEquals("ok", ioc.resolve("custom:test"));
    }

    @Test
    void testDependencyNotFound() {
        IoCContainer ioc = new IoCContainer();
        Exception e = assertThrows(RuntimeException.class, () -> ioc.resolve("not:found"));
        assertEquals("Dependency not found: not:found", e.getMessage());
    }
}
