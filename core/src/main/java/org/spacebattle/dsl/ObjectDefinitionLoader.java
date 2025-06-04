package org.spacebattle.dsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.spacebattle.repository.ObjectRepository;
import org.spacebattle.uobject.DefaultUObject;
import org.spacebattle.uobject.IUObject;
import org.spacebattle.ioc.IoC;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

/**
 * Загрузчик описаний объектов из YAML через универсальный источник {@link DslSource}.
 * Поддерживает формат:
 *
 * objects:
 *   ship-1:
 *     properties:
 *       location: { x: 0, y: 0 }
 *       velocity: { dx: 1, dy: 1 }
 *     capabilities:
 *       - move
 */
public class ObjectDefinitionLoader implements DslLoader<Void> {

    private final IoC ioc;

    public ObjectDefinitionLoader(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Основной метод загрузки DSL-описания объектов.
     * Реализация интерфейса {@link DslLoader}.
     */
    @Override
    public Void load(DslSource source) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream in = source.openStream()) {
            Map<String, Object> root = mapper.readValue(in, Map.class);
            loadObjectsFromMap(root);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void loadObjectsFromMap(Map<String, Object> root) {
        Map<String, Map<String, Object>> objects = (Map<String, Map<String, Object>>) root.get("objects");

        for (Map.Entry<String, Map<String, Object>> entry : objects.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> objectSpec = entry.getValue();

            ObjectDefinition def = new ObjectMapper().convertValue(objectSpec, ObjectDefinition.class);
            System.out.println("Загружен ObjectDefinition: " + id);
            System.out.println("   properties: " + def.properties);
            System.out.println("   capabilities: " + def.capabilities);


            IUObject obj = new DefaultUObject();
            def.properties.forEach((key, value) -> {
                obj.setProperty(key, normalizeProperty(key, value));
            });

            String key = "object:" + id;
            IUObject finalObj = obj;
            ObjectRepository repository = (ObjectRepository) ioc.resolve("object-repository");
            repository.save(id, obj);

            ioc.resolve("IoC.Register", key, (Function<Object[], Object>) args -> finalObj);

            System.out.println("Загружен объект из DSL: " + id);
        }
    }

    private Object normalizeProperty(String key, Object value) {
        String iocKey = "dsl:normalize:" + key;
        try {
            System.out.println("Попытка нормализации: " + key + " = " + value);
            Function<Object[], Object> normalizer = (Function<Object[], Object>) ioc.resolve(iocKey);
            Object result = normalizer.apply(new Object[]{value});
            System.out.println("Нормализовано: " + key + " -> " + result);
            return result;
        } catch (Exception e) {
            System.out.println("Нормализатор не найден для ключа: " + key + " (оставляем как есть)");
            System.out.println("Ошибка при нормализации свойства " + key + ": " + e);
            return value;
        }
    }

}
