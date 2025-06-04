package org.spacebattle.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setName("testuser");
        user.setPassword("secret");

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getName());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("test");
        u1.setPassword("pass");

        User u2 = new User();
        u2.setId(1L);
        u2.setName("test");
        u2.setPassword("pass");

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(42L);
        user.setName("admin");
        user.setPassword("admin");

        String output = user.toString();
        assertTrue(output.contains("admin"));
    }
}
