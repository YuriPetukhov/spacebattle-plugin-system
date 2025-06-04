package org.spacebattle.plugins.parsers;

import org.junit.jupiter.api.Test;
import org.spacebattle.plugins.parsers.models.ExceptionHandlerSpec;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerSpecParserTest {

    private final ExceptionHandlerSpecParser parser = new ExceptionHandlerSpecParser();

    @Test
    void parsesFromExceptionHandlers() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "exceptionHandlers", List.of(
                                Map.of("class", "org.spacebattle.TestHandler")
                        )
                )
        );

        List<ExceptionHandlerSpec> result = parser.parse(pluginSpec);

        assertEquals(1, result.size());
        assertEquals("org.spacebattle.TestHandler", result.get(0).className());
    }

    @Test
    void parsesFromExceptionsAsFallback() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "exceptions", List.of(
                                Map.of("class", "org.spacebattle.LegacyHandler")
                        )
                )
        );

        List<ExceptionHandlerSpec> result = parser.parse(pluginSpec);

        assertEquals(1, result.size());
        assertEquals("org.spacebattle.LegacyHandler", result.get(0).className());
    }

    @Test
    void returnsEmptyListIfNoPluginKey() {
        Map<String, Object> pluginSpec = Map.of("someOtherKey", List.of());
        assertTrue(parser.parse(pluginSpec).isEmpty());
    }

    @Test
    void returnsEmptyListIfNullInput() {
        assertTrue(parser.parse(null).isEmpty());
    }

    @Test
    void ignoresInvalidEntriesInList() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "exceptionHandlers", List.of(
                                Map.of("wrongKey", "noClass"),
                                Map.of("class", 12345),
                                Map.of("class", "valid.ClassName")
                        )
                )
        );

        List<ExceptionHandlerSpec> result = parser.parse(pluginSpec);

        assertEquals(1, result.size());
        assertEquals("valid.ClassName", result.get(0).className());
    }

    @Test
    void returnsEmptyIfHandlersNotList() {
        Map<String, Object> pluginSpec = Map.of(
                "plugin", Map.of(
                        "exceptionHandlers", "not-a-list"
                )
        );

        assertTrue(parser.parse(pluginSpec).isEmpty());
    }
}
