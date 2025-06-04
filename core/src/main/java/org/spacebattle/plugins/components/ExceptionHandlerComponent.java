package org.spacebattle.plugins.components;

import org.spacebattle.exceptions.DefaultExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandlerResolver;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.plugins.parsers.ExceptionHandlerSpecParser;
import org.spacebattle.plugins.parsers.PluginSpecParser;
import org.spacebattle.plugins.parsers.models.ExceptionHandlerSpec;

import java.util.List;
import java.util.Map;

public class ExceptionHandlerComponent implements PluginComponent {
    private final IoCContainer ioc;
    private final PluginSpecParser<ExceptionHandlerSpec> parser;

    public ExceptionHandlerComponent(IoCContainer ioc) {
        this.ioc = ioc;
        this.parser = new ExceptionHandlerSpecParser();
    }

    @Override
    public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
        List<ExceptionHandlerSpec> specs = parser.parse(pluginSpec);

        ExceptionHandler handler;

        if (specs.isEmpty()) {
            System.out.println("No exception handlers defined — using DefaultExceptionHandler");
            handler = new DefaultExceptionHandler(new Object[0]);
        } else {
            handler = instantiateHandler(loader, specs.get(0));
        }

        ExceptionHandler finalHandler = handler;
        ioc.register("exception-resolver", args -> (ExceptionHandlerResolver) source -> finalHandler);
        ioc.register("exception-handler", args -> finalHandler);
    }

    private ExceptionHandler instantiateHandler(ClassLoader loader, ExceptionHandlerSpec spec) {
        try {
            Class<?> clazz = loader.loadClass(spec.className());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof ExceptionHandler handler) {
                System.out.println("Registered exception handler: " + spec.className());
                return handler;
            } else {
                System.err.println("Class is not an ExceptionHandler: " + spec.className());
            }
        } catch (Exception e) {
            System.err.println("Failed to load exception handler: " + spec.className());
            e.printStackTrace();
        }

        return new DefaultExceptionHandler(new Object[0]);
    }
}
