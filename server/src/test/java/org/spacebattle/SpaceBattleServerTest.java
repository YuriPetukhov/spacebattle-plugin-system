package org.spacebattle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.setup.SpaceBattleInitializer;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тест для проверки инициализации IoC-контейнера через SpaceBattleInitializer.
 */
class SpaceBattleServerTest {

    @BeforeAll
    static void ensurePluginsDirectoryExists() {
        File pluginsDir = new File("plugins");
        if (!pluginsDir.exists()) {
            assertTrue(pluginsDir.mkdir(), "Не удалось создать директорию plugins для теста");
        }
    }


    @AfterAll
    static void cleanup() {
        File pluginsDir = new File("plugins");
        if (pluginsDir.exists()) {
            pluginsDir.delete();
        }
    }

    @Test
    void testInitializationRunsWithoutException() {
        IoCContainer ioc = new IoCContainer();
        assertNotNull(ioc, "IoC контейнер должен быть создан");

        assertDoesNotThrow(() -> SpaceBattleInitializer.initialize(ioc), "Инициализация не должна выбрасывать исключений");

        // Проверка: обработчик исключений зарегистрирован
        Object handler = ioc.resolve("exception-handler");
        assertNotNull(handler, "Обработчик исключений должен быть зарегистрирован");
        assertTrue(handler instanceof ExceptionHandler, "Обработчик должен реализовывать интерфейс ExceptionHandler");

        // Проверка: event-loop зарегистрирован и можно извлечь
        Object loop = ioc.resolve("event-loop");
        assertNotNull(loop, "EventLoop должен быть зарегистрирован");
        assertTrue(loop instanceof EventLoop, "EventLoop должен быть типа EventLoop");

        // Проверка: IoC зарегистрирован как зависимость
        Object self = ioc.resolve("ioc");
        assertSame(ioc, self, "IoC должен быть зарегистрирован сам в себе");
    }

    @Test
    void main_shouldRunWithoutExceptions() {
        assertDoesNotThrow(() -> SpaceBattleServer.main(new String[0]),
                "Вызов SpaceBattleServer.main() не должен выбрасывать исключений");
    }

    @Test
    void testMain_executesWithoutException() {
        assertDoesNotThrow(() -> {
            SpaceBattleServer.main(new String[]{});
        }, "Вызов main() не должен выбрасывать исключений");
    }

    @Test
    void class_shouldBeLoadable() {
        assertDoesNotThrow(() -> Class.forName("org.spacebattle.SpaceBattleServer"),
                "Класс SpaceBattleServer должен быть загружаемым");
    }

    @Test
    void shouldInstantiate() {
        assertDoesNotThrow(() -> new SpaceBattleServer(),
                "Создание экземпляра SpaceBattleServer не должно вызывать исключений");
    }
}
