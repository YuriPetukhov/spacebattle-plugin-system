package org.spacebattle.plugins.components;

import org.spacebattle.middleware.Middleware;
import org.spacebattle.middleware.MiddlewareRegistry;
import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.plugins.parsers.MiddlewareSpecParser;
import org.spacebattle.plugins.parsers.PluginSpecParser;
import org.spacebattle.plugins.parsers.models.MiddlewareSpec;

import java.util.Map;

public class MiddlewareComponent implements PluginComponent {
    private final MiddlewareRegistry registry;
    private final PluginSpecParser<MiddlewareSpec> parser;

    public MiddlewareComponent(MiddlewareRegistry registry) {
        this.registry = registry;
        this.parser = new MiddlewareSpecParser();
    }

    @Override
    public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
        for (MiddlewareSpec spec : parser.parse(pluginSpec)) {
            try {
                Class<?> clazz = loader.loadClass(spec.className());
                Middleware middleware = (Middleware) clazz.getDeclaredConstructor().newInstance();
                registry.register(middleware);
                System.out.println("Registered middleware: " + spec.className());
            } catch (Exception e) {
                throw new RuntimeException("Failed to load middleware: " + spec.className(), e);
            }
        }
    }
}
