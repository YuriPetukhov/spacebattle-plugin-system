package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameIdResponseTest {

    @Test
    void testGameIdGetter() {
        UUID id = UUID.randomUUID();
        GameIdResponse response = new GameIdResponse(id);

        assertEquals(id, response.gameId());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        GameIdResponse a = new GameIdResponse(id);
        GameIdResponse b = new GameIdResponse(id);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToStringContainsId() {
        UUID id = UUID.randomUUID();
        GameIdResponse response = new GameIdResponse(id);

        assertTrue(response.toString().contains(id.toString()));
    }
}
