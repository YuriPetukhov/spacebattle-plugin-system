package org.spacebattle.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngleTest {

    @Test
    void testNormalizationPositive() {
        Angle a = new Angle(370, 360);
        assertEquals(10, a.numerator());
        assertEquals(360, a.denominator());
    }

    @Test
    void testNormalizationNegative() {
        Angle a = new Angle(-30, 360);
        assertEquals(330, a.numerator());
        assertEquals(360, a.denominator());
    }

    @Test
    void testConstructorWithDegrees() {
        Angle a = new Angle(90);
        assertEquals(90, a.numerator());
        assertEquals(360, a.denominator());
    }

    @Test
    void testToDegrees() {
        assertEquals(90.0, new Angle(1, 4).toDegrees(), 0.001);
        assertEquals(270.0, new Angle(3, 4).toDegrees(), 0.001);
        assertEquals(0.0, new Angle(0, 360).toDegrees(), 0.001);
    }

    @Test
    void testAddSameDenominator() {
        Angle a = new Angle(90, 360);
        Angle b = new Angle(270, 360);
        a.add(b);
        assertEquals(0, a.numerator()); // (90 + 270) % 360 = 0
    }

    @Test
    void testAddImmutable() {
        Angle a = new Angle(90, 360);
        Angle b = new Angle(270, 360);
        Angle result = Angle.add(a, b);
        assertEquals(0, result.numerator());
        assertEquals(360, result.denominator());

        // исходные не изменились
        assertEquals(90, a.numerator());
        assertEquals(270, b.numerator());
    }

    @Test
    void testAddThrowsIfDenominatorMismatch() {
        Angle a = new Angle(1, 4);
        Angle b = new Angle(1, 6);
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
        assertThrows(IllegalArgumentException.class, () -> Angle.add(a, b));
    }

    @Test
    void testZeroDenominatorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Angle(10, 0));
    }

    @Test
    void testToStringFormat() {
        Angle a = new Angle(45, 360);
        assertEquals("Angle(45/360)", a.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Angle a = new Angle(90, 360);
        Angle b = new Angle(450, 360); // нормализуется в 90
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        Angle c = new Angle(91, 360);
        assertNotEquals(a, c);
    }
}
