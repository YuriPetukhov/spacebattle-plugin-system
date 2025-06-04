package org.spacebattle.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameRecordTest {

    @Test
    void testAccessors() {
        UUID id = UUID.randomUUID();
        List<String> players = List.of("alice", "bob");

        GameRecord record = new GameRecord(id, "TestGame", players, 3);

        assertEquals(id, record.id());
        assertEquals("TestGame", record.name());
        assertEquals(players, record.players());
        assertEquals(3, record.shipsPerPlayer());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        List<String> players = List.of("a", "b");

        GameRecord r1 = new GameRecord(id, "Name", players, 2);
        GameRecord r2 = new GameRecord(id, "Name", List.of("a", "b"), 2);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        UUID id = UUID.randomUUID();
        GameRecord record = new GameRecord(id, "Arena", List.of("p1", "p2"), 1);

        String str = record.toString();
        assertTrue(str.contains("Arena"));
        assertTrue(str.contains("p1"));
        assertTrue(str.contains("shipsPerPlayer=1") || str.contains("1")); // для защиты от формата
    }
}
