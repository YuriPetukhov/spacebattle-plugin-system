package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testAccessors() {
        LoginRequest request = new LoginRequest("admin", "1234");

        assertEquals("admin", request.username());
        assertEquals("1234", request.password());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginRequest r1 = new LoginRequest("u", "p");
        LoginRequest r2 = new LoginRequest("u", "p");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        LoginRequest req = new LoginRequest("x", "y");
        String str = req.toString();
        assertTrue(str.contains("x"));
        assertTrue(str.contains("y"));
    }
}
