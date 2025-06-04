package org.spacebattle.plugins.parsers;

import org.junit.jupiter.api.Test;
import org.spacebattle.plugins.parsers.models.MiddlewareSpec;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MiddlewareSpecParserTest {

    private final MiddlewareSpecParser parser = new MiddlewareSpecParser();

    @Test
    void parsesStringMiddlewares() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "middleware", List.of("org.example.MW1", "org.example.MW2")
                )
        );

        List<MiddlewareSpec> result = parser.parse(pluginSpec);

        assertEquals(2, result.size());
        assertEquals("org.example.MW1", result.get(0).className());
        assertEquals("org.example.MW2", result.get(1).className());
    }

    @Test
    void parsesObjectMiddlewares() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "middleware", List.of(
                                Map.of("class", "org.example.ObjectMW")
                        )
                )
        );

        List<MiddlewareSpec> result = parser.parse(pluginSpec);

        assertEquals(1, result.size());
        assertEquals("org.example.ObjectMW", result.get(0).className());
    }

    @Test
    void parsesMixedMiddlewares() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "middleware", List.of(
                                "org.example.MW1",
                                Map.of("class", "org.example.MW2")
                        )
                )
        );

        List<MiddlewareSpec> result = parser.parse(pluginSpec);

        assertEquals(2, result.size());
        assertEquals("org.example.MW1", result.get(0).className());
        assertEquals("org.example.MW2", result.get(1).className());
    }

    @Test
    void returnsEmptyListIfNoMiddlewareKey() {
        Map<String, Object> pluginSpec = Map.of("plugin", Map.of());
        List<MiddlewareSpec> result = parser.parse(pluginSpec);
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyListIfNoPluginBlock() {
        Map<String, Object> pluginSpec = Map.of("other", "value");
        List<MiddlewareSpec> result = parser.parse(pluginSpec);
        assertTrue(result.isEmpty());
    }

    @Test
    void throwsExceptionOnInvalidFormat() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "middleware", List.of(
                                123, // невалидный тип
                                Map.of("notClass", "x")
                        )
                )
        );

        assertThrows(RuntimeException.class, () -> parser.parse(pluginSpec));
    }
}
