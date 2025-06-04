package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenRequestTest {

    @Test
    void testFieldAccess() {
        UUID gameId = UUID.randomUUID();
        TokenRequest request = new TokenRequest("user123", gameId);

        assertEquals("user123", request.username());
        assertEquals(gameId, request.gameId());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID gameId = UUID.randomUUID();
        TokenRequest r1 = new TokenRequest("user", gameId);
        TokenRequest r2 = new TokenRequest("user", gameId);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringIncludesFields() {
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        TokenRequest req = new TokenRequest("x", id);
        String out = req.toString();
        assertTrue(out.contains("x"));
        assertTrue(out.contains("123e4567-e89b-12d3-a456-426614174000"));
    }
}
