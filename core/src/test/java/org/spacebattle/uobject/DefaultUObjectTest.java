package org.spacebattle.uobject;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUObjectTest {

    @Test
    void testSetAndGetProperty() {
        IUObject obj = new DefaultUObject();

        obj.setProperty("health", 100);
        obj.setProperty("name", "ship");

        assertEquals(Optional.of(100), obj.getProperty("health"));
        assertEquals(Optional.of("ship"), obj.getProperty("name"));
    }

    @Test
    void testOverwriteProperty() {
        IUObject obj = new DefaultUObject();

        obj.setProperty("score", 10);
        obj.setProperty("score", 50);

        assertEquals(Optional.of(50), obj.getProperty("score"));
    }

    @Test
    void testMissingPropertyReturnsEmptyOptional() {
        IUObject obj = new DefaultUObject();

        assertTrue(obj.getProperty("nonexistent").isEmpty());
    }

    @Test
    void testNullValueIsStoredAndReturnedAsEmpty() {
        IUObject obj = new DefaultUObject();

        obj.setProperty("key", null);
        Optional<Object> result = obj.getProperty("key");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGenericTypeCasting() {
        IUObject obj = new DefaultUObject();
        obj.setProperty("x", 42);

        int x = (int)obj.getProperty("x").orElseThrow();
        assertEquals(42, x);
    }
}
