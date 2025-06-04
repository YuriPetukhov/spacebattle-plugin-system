package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameCreationRequestTest {

    @Test
    void testGetPlayerUsernames() {
        List<String> names = List.of("alice", "bob");
        GameCreationRequest request = new GameCreationRequest(names);

        assertEquals(names, request.playerUsernames());
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> names = List.of("a", "b");
        GameCreationRequest a = new GameCreationRequest(names);
        GameCreationRequest b = new GameCreationRequest(List.of("a", "b"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToStringContainsNames() {
        List<String> names = List.of("x", "y");
        GameCreationRequest request = new GameCreationRequest(names);

        assertTrue(request.toString().contains("x"));
        assertTrue(request.toString().contains("y"));
    }
}
