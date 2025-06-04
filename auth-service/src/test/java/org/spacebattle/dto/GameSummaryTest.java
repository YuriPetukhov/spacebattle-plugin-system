package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameSummaryTest {

    @Test
    void testAccessors() {
        UUID id = UUID.randomUUID();
        GameSummary summary = new GameSummary(id, "Test Game");

        assertEquals(id, summary.id());
        assertEquals("Test Game", summary.name());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        GameSummary s1 = new GameSummary(id, "Name");
        GameSummary s2 = new GameSummary(id, "Name");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        UUID id = UUID.randomUUID();
        GameSummary summary = new GameSummary(id, "Arena");

        String str = summary.toString();
        assertTrue(str.contains("Arena"));
        assertTrue(str.contains(id.toString()));
    }
}
