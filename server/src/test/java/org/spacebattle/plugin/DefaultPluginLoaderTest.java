
package org.spacebattle.plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.util.PluginYamlUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPluginLoaderTest {

    private PluginScanner scanner;
    private PluginProcessor processor;
    private PluginComponent component;
    private DefaultPluginLoader loader;

    @BeforeEach
    void setUp() {
        scanner = mock(PluginScanner.class);
        processor = mock(PluginProcessor.class);
        component = mock(PluginComponent.class);
        loader = new DefaultPluginLoader(scanner, processor, List.of(component));
    }

    @AfterEach
    void cleanupTempJars() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File[] jarFiles = tmpDir.listFiles((dir, name) -> name.startsWith("testPlugin") && name.endsWith(".jar"));

        if (jarFiles != null) {
            for (File jar : jarFiles) {
                jar.delete();
            }
        }
    }

    @Test
    void loadAllFrom_shouldCallProcessorForEachJar() throws Exception {
        File fakeJar = File.createTempFile("plugin", ".jar");
        when(scanner.findJars(any())).thenReturn(List.of(fakeJar));

        loader.loadAllFrom(new File("someDir"));

        verify(processor).process(fakeJar);
    }

    @Test
    void createIsolatedClassLoader_shouldCacheClassLoader() throws Exception {
        File jar = new File("build/libs/fake-plugin.jar");

        DefaultPluginLoader loader = new DefaultPluginLoader(
                mock(PluginScanner.class),
                mock(PluginProcessor.class),
                List.of()
        );

        Method method = DefaultPluginLoader.class.getDeclaredMethod("createIsolatedClassLoader", File.class);
        method.setAccessible(true);

        URLClassLoader loader1 = (URLClassLoader) method.invoke(loader, jar);
        URLClassLoader loader2 = (URLClassLoader) method.invoke(loader, jar);

        assertSame(loader1, loader2);
    }


    @Test
    void loadPlugin_shouldApplyComponents() throws Exception {
        File jar = createMinimalJarWithYaml("test-plugin");

        PluginComponent component = mock(PluginComponent.class);
        PluginScanner scanner = mock(PluginScanner.class);
        PluginProcessor processor = mock(PluginProcessor.class);
        List<PluginComponent> components = List.of(component);

        Map<String, Object> mockYaml = Map.of("plugin", Map.of("middleware", List.of()));

        try (MockedStatic<PluginYamlUtil> pluginYamlUtilMock = mockStatic(PluginYamlUtil.class)) {
            pluginYamlUtilMock.when(() -> PluginYamlUtil.loadYamlFromJar(any()))
                    .thenReturn(mockYaml);

            pluginYamlUtilMock.when(() -> PluginYamlUtil.applyDynamicComponents(any(), any()))
                    .thenAnswer(invocation -> null);

            DefaultPluginLoader loader = new DefaultPluginLoader(scanner, processor, components);
            loader.loadPlugin(jar);

            verify(component).apply(any(), eq(mockYaml));
            pluginYamlUtilMock.verify(() -> PluginYamlUtil.applyDynamicComponents(any(), any()));
        }
    }



    private File createMinimalJarWithYaml(String name) throws IOException {
        File tempJar = Files.createTempFile(name, ".jar").toFile();
        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(tempJar))) {
            JarEntry entry = new JarEntry("plugin.yaml");
            out.putNextEntry(entry);
            out.write("plugin: {}".getBytes());
            out.closeEntry();
        }
        return tempJar;
    }
}
