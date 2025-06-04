package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void testFieldValues() {
        RegistrationRequest req = new RegistrationRequest("testuser", "pass");
        assertEquals("testuser", req.username());
        assertEquals("pass", req.password());
    }

    @Test
    void testEqualityAndHashCode() {
        RegistrationRequest r1 = new RegistrationRequest("a", "b");
        RegistrationRequest r2 = new RegistrationRequest("a", "b");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringOutput() {
        RegistrationRequest req = new RegistrationRequest("a", "b");
        String s = req.toString();
        assertTrue(s.contains("a"));
        assertTrue(s.contains("b"));
    }
}
