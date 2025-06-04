package org.spacebattle.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    @Test
    void testValidVectorCreation() {
        Vector v = new Vector(3, 5);
        assertEquals(3, v.dx());
        assertEquals(5, v.dy());
    }

    @Test
    void testZeroVectorIsAllowed() {
        Vector v = new Vector(0, 0);
        assertEquals(0, v.dx());
        assertEquals(0, v.dy());
    }

    @Test
    void testNegativeDxThrows() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Vector(-1, 2);
        });
        assertEquals("Velocity cannot be negative", ex.getMessage());
    }

    @Test
    void testNegativeDyThrows() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Vector(1, -2);
        });
        assertEquals("Velocity cannot be negative", ex.getMessage());
    }

    @Test
    void testBothNegativeThrows() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new Vector(-1, -1);
        });
        assertEquals("Velocity cannot be negative", ex.getMessage());
    }
}
