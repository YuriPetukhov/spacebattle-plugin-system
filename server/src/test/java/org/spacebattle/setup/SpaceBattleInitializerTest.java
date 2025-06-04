package org.spacebattle.setup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.commands.Command;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.plugin.PluginLoader;
import org.spacebattle.repository.DefaultObjectRepository;
import org.spacebattle.repository.InMemoryObjectRepository;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SpaceBattleInitializerTest {

    private IoCContainer ioc;
    private File pluginsDir;

    @BeforeEach
    void setUp() {
        ioc = new IoCContainer();
        pluginsDir = new File("plugins");
        if (!pluginsDir.exists()) {
            pluginsDir.mkdir();
        }
    }

    @AfterEach
    void tearDown() {
        if (pluginsDir.exists()) {
            for (File file : pluginsDir.listFiles()) {
                file.delete();
            }
            pluginsDir.delete();
        }
    }

    @Test
    void testInitialize_registersIoCRegister() throws Exception {
        SpaceBattleInitializer.initialize(ioc);

        Object result = ioc.resolve("IoC.Register", "key", (Function<Object[], Object>) args -> null);
        assertNull(result);
    }

    @Test
    void testInitialize_registersExceptionHandler() throws Exception {
        SpaceBattleInitializer.initialize(ioc);

        Object handler = ioc.resolve("exception-handler");
        assertNotNull(handler);
        assertTrue(handler instanceof ExceptionHandler);
    }

    @Test
    void testInitialize_registersRepository() throws Exception {
        SpaceBattleInitializer.initialize(ioc);

        Object repo = ioc.resolve("object-repository");
        assertNotNull(repo);
        assertTrue(repo instanceof DefaultObjectRepository);
    }

    @Test
    void testInitialize_registersCommandDispatcher() throws Exception {
        SpaceBattleInitializer.initialize(ioc);

        Object dispatcher = ioc.resolve("command-dispatcher");
        assertNotNull(dispatcher);
        assertTrue(dispatcher instanceof Function);

        // Проверка, что команда выполняется
        Command cmd = mock(Command.class);
        ((Function<Command, Void>) dispatcher).apply(cmd);
        verify(cmd).execute();
    }

    @Test
    void testInitialize_startsPluginWatcherThread_mockedLoader() throws Exception {
        // Подготовка папки plugins
        File pluginsDir = new File("plugins");
        if (!pluginsDir.exists()) {
            pluginsDir.mkdir();
        }

        try {
            PluginLoader mockLoader = mock(PluginLoader.class);

            Thread watcherThread = SpaceBattleInitializer.initialize(ioc, mockLoader);

            assertNotNull(watcherThread);
            assertEquals("PluginWatcher", watcherThread.getName());
            assertTrue(watcherThread.isAlive());

            // Проверим, что mock вызывался
            verify(mockLoader).loadAllFrom(any(File.class));
        } finally {
            // Удаляем папку plugins после теста
            for (File f : pluginsDir.listFiles()) {
                f.delete();
            }
            pluginsDir.delete();
        }
    }


    @Test
    void testInitialize_handlesMissingPluginsGracefully() {
        assertDoesNotThrow(() -> SpaceBattleInitializer.initialize(ioc));
    }

    @Test
    void testDslNormalizersAreRegistered() throws Exception {
        SpaceBattleInitializer.initialize(ioc);

        Function<Object[], Object> normLocation = (Function<Object[], Object>) ioc.resolve("dsl:normalize:location");
        Object result = normLocation.apply(new Object[]{Map.of("x", 10, "y", 20)});
        assertEquals("Point[x=10, y=20]", result.toString());

        Function<Object[], Object> normVelocity = (Function<Object[], Object>) ioc.resolve("dsl:normalize:velocity");
        Object v = normVelocity.apply(new Object[]{Map.of("dx", 3, "dy", 4)});
        assertEquals("Vector[dx=3, dy=4]", v.toString());

        Function<Object[], Object> normAngle = (Function<Object[], Object>) ioc.resolve("dsl:normalize:angle");
        Object a = normAngle.apply(new Object[]{Map.of("angle", 90, "max", 360)});
        assertEquals("Angle(90/360)", a.toString());
    }

    @Test
    void testEventLoopIsStarted() throws Exception {
        SpaceBattleInitializer.initialize(ioc);
        Object loop = ioc.resolve("event-loop");
        assertNotNull(loop);
    }

    @Test
    void testCreateDefaultPluginLoader_returnsDefaultLoader() throws Exception {
        PluginLoader loader = SpaceBattleInitializerTestProxy.createDefaultPluginLoader(ioc);
        assertNotNull(loader);
    }

    public static class SpaceBattleInitializerTestProxy {
        public static PluginLoader createDefaultPluginLoader(IoCContainer ioc) throws Exception {
            var method = SpaceBattleInitializer.class
                    .getDeclaredMethod("createDefaultPluginLoader", IoCContainer.class);
            method.setAccessible(true);
            return (PluginLoader) method.invoke(null, ioc);
        }
    }



}
