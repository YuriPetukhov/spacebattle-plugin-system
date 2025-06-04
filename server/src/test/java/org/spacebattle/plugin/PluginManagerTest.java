package org.spacebattle.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PluginManagerTest {

    private PluginManager manager;

    @BeforeEach
    void setUp() {
        manager = new PluginManager();
    }

    @Test
    void testListLoadedPluginsInitiallyEmpty() {
        Set<String> plugins = manager.listLoadedPlugins();
        assertNotNull(plugins);
        assertTrue(plugins.isEmpty());
    }

    @Test
    void testPluginEntryStoresJarAndClassLoader() throws Exception {
        File file = new File("test-plugin.jar");
        URLClassLoader loader = new URLClassLoader(new java.net.URL[]{});

        PluginManager.PluginEntry entry = new PluginManager.PluginEntry(file, loader);

        assertEquals(file, entry.jarFile);
        assertEquals(loader, entry.classLoader);
    }

    @Test
    void testLoadUnloadReloadStub() {
        File dummyJar = new File("fake.jar");

        assertDoesNotThrow(() -> manager.loadPlugin(dummyJar));
        assertDoesNotThrow(() -> manager.reloadPlugin(dummyJar));
        assertDoesNotThrow(() -> manager.unloadPlugin("dummy"));
    }
}

