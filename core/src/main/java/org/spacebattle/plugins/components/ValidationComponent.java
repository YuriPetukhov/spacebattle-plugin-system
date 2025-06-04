//package org.spacebattle.plugins.components;
//
//import org.spacebattle.plugins.PluginComponent;
//import org.spacebattle.security.PluginValidator;
//
//import java.io.File;
//import java.net.URLClassLoader;
//import java.security.PublicKey;
//import java.util.Map;
//
///**
// * Компонент валидации плагинов.
// * Использует {@link PluginValidator} для проверки подписи jar-файла
// * до загрузки плагина.
// */
//public class ValidationComponent implements PluginComponent {
//
//    private final PluginValidator validator;
//
//    /**
//     * Создаёт компонент, выполняющий проверку подписи плагина.
//     *
//     * @param validator  объект, реализующий логику валидации подписи
//     */
//    public ValidationComponent(PluginValidator validator) {
//        this.validator = validator;
//    }
//
//    /**
//     * Вызывается системой плагинов перед регистрацией других компонентов.
//     * Получает загрузчик и спецификацию из plugin.yaml.
//     * Проверяет подлинность jar-файла плагина.
//     *
//     * @param loader      загрузчик класса, связанный с текущим плагином
//     * @param pluginSpec  данные из plugin.yaml
//     * @throws RuntimeException если валидация не пройдена
//     */
//    @Override
//    public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
//        try {
//            if (!(loader instanceof URLClassLoader)) {
//                throw new IllegalArgumentException("Unsupported class loader: " + loader.getClass());
//            }
//
//            URLClassLoader urlClassLoader = (URLClassLoader) loader;
//            File jarFile = new File(urlClassLoader.getURLs()[0].toURI());
//
//            validator.validate(jarFile);
//        } catch (Exception e) {
//            throw new RuntimeException("Plugin validation failed", e);
//        }
//    }
//}
//
