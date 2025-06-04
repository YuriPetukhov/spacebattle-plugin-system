package org.spacebattle.plugins.components;

import org.spacebattle.ioc.IoC;
import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.plugins.parsers.CommandSpecParser;
import org.spacebattle.plugins.parsers.PluginSpecParser;
import org.spacebattle.plugins.parsers.models.CommandSpec;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

public class CommandComponent implements PluginComponent {
    private final IoC ioc;
    private final PluginSpecParser<CommandSpec> parser;

    public CommandComponent(IoC ioc) {
        this.ioc = ioc;
        this.parser = new CommandSpecParser();
    }

    @Override
    public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
        for (CommandSpec spec : parser.parse(pluginSpec)) {
            if (!"factory".equalsIgnoreCase(spec.type())) continue;

            try {
                // ЗАГРУЖАЕМ фабрику через loader (а не Class.forName / this.getClass().getClassLoader)
                Class<?> factoryClass = loader.loadClass(spec.className());
                System.out.println("factoryClass: " + factoryClass.getName());
                Object factory = factoryClass.getDeclaredConstructor().newInstance();

                //  Внедряем IoC, если метод есть
                try {
                    Method setIoC = factoryClass.getMethod("setIoC", IoC.class);
                    setIoC.invoke(factory, ioc);
                } catch (NoSuchMethodException ignored) {}

                // Регистрируем фабрику
                ioc.resolve("IoC.Register", "command:" + spec.name(),
                        (Function<Object[], Object>) args -> factory);

                System.out.println("Registered command factory: " + spec.name() + "name: " + spec.className());
            } catch (Exception e) {
                throw new RuntimeException("Failed to register command: " + spec.name(), e);
            }
        }
    }
}
