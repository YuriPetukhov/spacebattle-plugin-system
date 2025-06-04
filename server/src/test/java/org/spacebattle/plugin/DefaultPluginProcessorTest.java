package org.spacebattle.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.util.PluginYamlUtil;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class DefaultPluginProcessorTest {

    private ClassLoaderFactory loaderFactory;
    private PluginComponent component;
    private DefaultPluginProcessor processor;

    @BeforeEach
    void setUp() {
        loaderFactory = mock(ClassLoaderFactory.class);
        component = mock(PluginComponent.class);
        processor = new DefaultPluginProcessor(List.of(component), loaderFactory);
    }

    @Test
    void testProcessLoadsYamlAndAppliesComponents() throws Exception {
        File jarFile = mock(File.class);
        URLClassLoader classLoader = mock(URLClassLoader.class);

        when(jarFile.getCanonicalPath()).thenReturn("dummy-path");
        when(loaderFactory.create(jarFile)).thenReturn(classLoader);

        Map<String, Object> dummyYaml = Map.of("plugin", Map.of("key", "value"));

        try (MockedStatic<PluginYamlUtil> mocked = mockStatic(PluginYamlUtil.class)) {
            mocked.when(() -> PluginYamlUtil.loadYamlFromJar(jarFile)).thenReturn(dummyYaml);
            mocked.when(() -> PluginYamlUtil.applyDynamicComponents(eq(classLoader), any()))
                    .then(invocation -> null);

            processor.process(jarFile);

            verify(loaderFactory).create(jarFile);
            verify(component).apply(eq(classLoader), eq(dummyYaml));
            mocked.verify(() -> PluginYamlUtil.applyDynamicComponents(eq(classLoader), any()));
        }
    }

}
