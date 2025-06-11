package org.spacebattle.plugins.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.DefaultExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoCContainer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BootstrapComponentTest {

    private BootstrapComponent component;
    private IoCContainer ioc;

    @BeforeEach
    void setup() {
        ioc = new IoCContainer();
        ioc.register("exception-handler", DefaultExceptionHandler::new);
        component = new BootstrapComponent(ioc);
    }

    @Test
    void testRunMethodIsInvoked() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "bootstrap", List.of(
                                Map.of("class", TestRunClass.class.getName())
                        )
                )
        );

        TestRunClass.invoked = false;
        component.apply(getClass().getClassLoader(), pluginSpec);
        assertTrue(TestRunClass.invoked, "run() method should be invoked");
    }

    @Test
    void testIoCInjectionIsPerformed() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "bootstrap", List.of(
                                Map.of("class", IoCInjectionClass.class.getName())
                        )
                )
        );

        IoCInjectionClass.iocInjected = false;
        component.apply(getClass().getClassLoader(), pluginSpec);
        assertTrue(IoCInjectionClass.iocInjected, "IoC should be injected");
    }

    @Test
    void testExceptionHandlerInjectionIsPerformed() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "bootstrap", List.of(
                                Map.of("class", ExceptionHandlerInjectionClass.class.getName())
                        )
                )
        );

        ExceptionHandlerInjectionClass.handlerInjected = false;
        component.apply(getClass().getClassLoader(), pluginSpec);
        assertTrue(ExceptionHandlerInjectionClass.handlerInjected, "ExceptionHandler should be injected");
    }

    @Test
    void testNoRunMethodHandledGracefully() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "bootstrap", List.of(
                                Map.of("class", NoRunMethodClass.class.getName())
                        )
                )
        );

        assertDoesNotThrow(() -> component.apply(getClass().getClassLoader(), pluginSpec));
    }

    @Test
    void testMissingPluginKeyDoesNothing() {
        Map<String, Object> pluginSpec = Map.of();
        assertDoesNotThrow(() -> component.apply(getClass().getClassLoader(), pluginSpec));
    }

    @Test
    void testMissingBootstrapKeyDoesNothing() {
        Map<String, Object> pluginSpec = Map.of("plugin", Map.of()); // no "bootstrap"
        assertDoesNotThrow(() -> component.apply(getClass().getClassLoader(), pluginSpec));
    }

    @Test
    void testInvalidClassThrows() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "bootstrap", List.of(
                                Map.of("class", "non.existent.ClassName")
                        )
                )
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> component.apply(getClass().getClassLoader(), pluginSpec));
        assertTrue(ex.getMessage().contains("Failed to bootstrap"));
    }

    // ===== Вспомогательные классы =====
    public static class TestRunClass {
        public static boolean invoked = false;

        public void run() {
            invoked = true;
        }
    }

    public static class IoCInjectionClass {
        public static boolean iocInjected = false;

        public void setIoC(org.spacebattle.ioc.IoC ioc) {
            iocInjected = ioc != null;
        }
    }

    public static class ExceptionHandlerInjectionClass {
        public static boolean handlerInjected = false;

        public void setExceptionHandler(ExceptionHandler handler) {
            handlerInjected = handler != null;
        }
    }

    public static class NoRunMethodClass {
        // no run()
    }
}
