package org.spacebattle.plugins.components;

import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.ioc.IoC;
import org.spacebattle.plugins.PluginComponent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Компонент плагина, загружающий и инициализирующий bootstrap-классы.
 * Поддерживает внедрение IoC и ExceptionHandler, если соответствующие set-методы присутствуют.
 */
public class BootstrapComponent implements PluginComponent {

    private final IoC ioc;

    public BootstrapComponent(IoC ioc) {
        this.ioc = ioc;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
        Map<String, Object> plugin = (Map<String, Object>) pluginSpec.get("plugin");
        if (plugin == null || !plugin.containsKey("bootstrap")) return;

        List<Map<String, String>> bootstrapList = (List<Map<String, String>>) plugin.get("bootstrap");

        for (Map<String, String> entry : bootstrapList) {
            String className = entry.get("class");
            try {
                Class<?> clazz = loader.loadClass(className);
                Object instance = clazz.getDeclaredConstructor().newInstance();

                injectIfPresent(clazz, instance, "setIoC", IoC.class, ioc);
                injectIfPresent(clazz, instance, "setExceptionHandler", ExceptionHandler.class,
                        (ExceptionHandler) ioc.resolve("exception-handler"));

                invokeIfExists(clazz, instance, "run");

            } catch (Exception e) {
                throw new RuntimeException("Failed to bootstrap class: " + className, e);
            }
        }
    }

    private void injectIfPresent(Class<?> clazz, Object instance, String methodName, Class<?> paramType, Object value) {
        try {
            Method method = clazz.getMethod(methodName, paramType);
            method.invoke(instance, value);
            System.out.println("Внедрено " + paramType.getSimpleName() + " в: " + clazz.getName());
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Не удалось внедрить зависимость: " + paramType.getSimpleName(), e);
        }
    }

    private void invokeIfExists(Class<?> clazz, Object instance, String methodName) {
        try {
            Method method = clazz.getMethod(methodName);
            method.invoke(instance);
            System.out.println("Bootstrap: вызван метод " + methodName + " в " + clazz.getName());
        } catch (NoSuchMethodException ignored) {
            System.out.println("Метод " + methodName + " не найден в " + clazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при вызове " + methodName + " в " + clazz.getName(), e);
        }
    }
}
