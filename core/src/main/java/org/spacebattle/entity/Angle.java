package org.spacebattle.entity;

/**
 * Класс представляет угол в виде дроби d/n оборота (где полный оборот = 1.0).
 * Например, Angle(1, 4) = 90°, Angle(3, 4) = 270°.
 */
public class Angle {
    private int d;      // числитель (положение угла)
    private final int n; // знаменатель (деления круга)

    /**
     * Создаёт нормализованный угол с заданным числителем и знаменателем.
     * Угол всегда нормализуется в диапазон [0, n).
     *
     * @param d числитель
     * @param n знаменатель (должен быть > 0)
     */
    public Angle(int d, int n) {
        if (n <= 0) throw new IllegalArgumentException("Denominator must be positive");
        this.n = n;
        this.d = ((d % n) + n) % n; // нормализация в положительный диапазон
    }

    public Angle(int degrees) {
        this(degrees, 360); // один полный оборот = 360 делений
    }


    public int numerator() { return d; }

    public int denominator() { return n; }

    /**
     * Прибавляет другой угол с тем же знаменателем (модульно).
     */
    public void add(Angle other) {
        if (this.n != other.n) throw new IllegalArgumentException("Denominators must match");
        this.d = (this.d + other.d) % this.n;
    }

    public static Angle add(Angle a, Angle b) {
        if (a.n != b.n) throw new IllegalArgumentException("Denominators must match");
        return new Angle(a.d + b.d, a.n);
    }

    /**
     * Преобразует в градусы (0–360).
     */
    public double toDegrees() {
        return 360.0 * d / n;
    }

    @Override
    public String toString() {
        return "Angle(" + d + "/" + n + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Angle angle = (Angle) obj;
        return this.d == angle.d && this.n == angle.n;
    }

    @Override
    public int hashCode() {
        return 31 * d + n;
    }

}
