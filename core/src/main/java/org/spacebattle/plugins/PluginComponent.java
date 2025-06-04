package org.spacebattle.plugins;

import java.util.Map;

/**
 * Компонент плагина, выполняющий специфическое действие при загрузке плагина.
 * <p>
 * Компоненты могут регистрировать команды, middleware, обработчики исключений и т.д.
 * Используется как часть фазы инициализации плагина.
 * <p>
 * Каждый компонент реализует метод {@link #apply(ClassLoader, Map)}, который вызывается
 * при загрузке плагина и получает доступ к его конфигурации и classloader'у.
 */
public interface PluginComponent {

    /**
     * Метод, вызываемый при применении компонента плагина.
     *
     * @param loader      classloader, из которого загружается плагин. Позволяет загружать
     *                    классы, указанные в DSL-конфигурации.
     * @param pluginSpec  карта с данными плагина, полученная из YAML/JSON DSL.
     *                    Обычно содержит секции plugin.commands, plugin.middleware и др.
     */
    void apply(ClassLoader loader, Map<String, Object> pluginSpec);
}
