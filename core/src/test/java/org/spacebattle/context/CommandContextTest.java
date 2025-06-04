package org.spacebattle.context;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommandContextTest {

    @Test
    void testPutAndGet_existingKey_returnsValue() {
        CommandContext context = new CommandContext();
        context.put("user", "Alice");

        Optional<String> result = context.get("user");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get());
    }

    @Test
    void testGet_nonExistingKey_returnsEmpty() {
        CommandContext context = new CommandContext();

        Optional<String> result = context.get("unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void testGet_existingKeyWithWrongType_throwsClassCastException() {
        CommandContext context = new CommandContext();
        context.put("number", 42);

        assertThrows(ClassCastException.class, () -> {
            // Извлекаем как Object, а потом явно приводим к String
            String result = (String) context.get("number").get();
        });
    }


    @Test
    void testAsMap_returnsInternalState() {
        CommandContext context = new CommandContext();
        context.put("key", "value");

        Map<String, Object> map = context.asMap();

        assertEquals(1, map.size());
        assertEquals("value", map.get("key"));
    }

    @Test
    void testMultipleEntries() {
        CommandContext context = new CommandContext();
        context.put("a", 1);
        context.put("b", true);
        context.put("c", "value");

        assertEquals(3, context.asMap().size());
        assertEquals(1, context.<Integer>get("a").get());
        assertEquals(true, context.<Boolean>get("b").get());
        assertEquals("value", context.<String>get("c").get());
    }
}
