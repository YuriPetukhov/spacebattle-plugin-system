package org.spacebattle;

import org.spacebattle.plugins.PluginComponent;

import java.util.Map;

/**
 * Демонстрационный компонент плагина, выводящий приветственное сообщение при загрузке.
 * Используется для тестирования механизма динамического подключения PluginComponent.
 */
public class HelloComponent implements PluginComponent {

    /**
     * Метод вызывается при загрузке плагина. Выводит сообщение в консоль.
     * @param loader загрузчик классов, переданный плагину (не используется здесь)
     * @param pluginSpec десериализованное содержимое plugin.yaml
     */
    @Override
    public void apply(ClassLoader loader, Map<String, Object> pluginSpec) {
        System.out.println("Hello from HelloComponent inside plugin!");
    }
}
