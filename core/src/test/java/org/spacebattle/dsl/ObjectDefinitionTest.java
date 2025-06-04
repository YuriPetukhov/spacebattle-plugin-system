package org.spacebattle.dsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDefinitionTest {

    @Test
    void testDeserializationFromYaml() throws Exception {
        String yaml = """
            properties:
              location:
                x: 5
                y: 10
              name: test-ship
            capabilities:
              - move
              - rotate
            """;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ObjectDefinition def = mapper.readValue(yaml, ObjectDefinition.class);

        // Проверка свойств
        assertNotNull(def.properties);
        assertTrue(def.properties.containsKey("location"));
        Map<String, Integer> location = (Map<String, Integer>) def.properties.get("location");
        assertEquals(5, location.get("x"));
        assertEquals(10, location.get("y"));
        assertEquals("test-ship", def.properties.get("name"));

        // Проверка способностей
        assertNotNull(def.capabilities);
        assertEquals(List.of("move", "rotate"), def.capabilities);
    }
}
