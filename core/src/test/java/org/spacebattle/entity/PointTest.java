package org.spacebattle.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void testPlusPositiveVector() {
        Point p = new Point(10, 20);
        Vector v = new Vector(3, 4);

        Point result = Point.plus(p, v);

        assertEquals(13, result.x());
        assertEquals(24, result.y());
    }

    @Test
    void testPlusNegativeVector() {
        Point p = new Point(5, 5);
        Vector v = new Vector(2, 7);

        Point result = Point.plus(p, v);

        assertEquals(7, result.x());
        assertEquals(12, result.y());
    }

    @Test
    void testPlusZeroVector() {
        Point p = new Point(8, 9);
        Vector v = new Vector(0, 0);

        Point result = Point.plus(p, v);

        assertEquals(p, result); // объектно-эквивалентная точка
    }
}
