package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessRegistryTest {

    private AccessRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new AccessRegistry();
    }

    @Test
    void isAllowed_shouldReturnFalse_whenNoAccessGranted() {
        assertFalse(registry.isAllowed("user1", "obj1"));
    }

    @Test
    void isAllowed_shouldReturnTrue_afterAllowCalled() {
        registry.allow("user1", "obj1");

        assertTrue(registry.isAllowed("user1", "obj1"));
    }

    @Test
    void isAllowed_shouldReturnFalse_forDifferentObject() {
        registry.allow("user1", "obj1");

        assertFalse(registry.isAllowed("user1", "obj2"));
    }

    @Test
    void isAllowed_shouldReturnFalse_forDifferentUser() {
        registry.allow("user1", "obj1");

        assertFalse(registry.isAllowed("user2", "obj1"));
    }

    @Test
    void allow_shouldSupportMultipleObjectsPerUser() {
        registry.allow("user1", "obj1");
        registry.allow("user1", "obj2");

        assertTrue(registry.isAllowed("user1", "obj1"));
        assertTrue(registry.isAllowed("user1", "obj2"));
    }

    @Test
    void allow_shouldNotAffectOtherUsers() {
        registry.allow("user1", "obj1");
        registry.allow("user2", "obj2");

        assertTrue(registry.isAllowed("user1", "obj1"));
        assertFalse(registry.isAllowed("user1", "obj2"));

        assertTrue(registry.isAllowed("user2", "obj2"));
        assertFalse(registry.isAllowed("user2", "obj1"));
    }
}
