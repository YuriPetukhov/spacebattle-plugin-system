package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenResponseTest {

    @Test
    void testFieldAccess() {
        List<String> objects = List.of("id1", "id2");
        TokenResponse response = new TokenResponse("abc123", objects);

        assertEquals("abc123", response.token());
        assertEquals(objects, response.objectIds());
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> ids = List.of("id1", "id2");
        TokenResponse r1 = new TokenResponse("tok", ids);
        TokenResponse r2 = new TokenResponse("tok", ids);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        TokenResponse r = new TokenResponse("t123", List.of("a", "b"));
        String out = r.toString();

        assertTrue(out.contains("t123"));
        assertTrue(out.contains("a"));
        assertTrue(out.contains("b"));
    }
}
