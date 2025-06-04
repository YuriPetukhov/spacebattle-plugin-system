package org.spacebattle.entity;

/**
 * Представляет точку на двумерной плоскости с целыми координатами.
 *
 * @param x координата X
 * @param y координата Y
 */
public record Point(int x, int y) {

    /**
     * Складывает точку с вектором, возвращая новую точку.
     *
     * @param p исходная точка
     * @param v вектор (смещение)
     * @return новая точка: (p.x + v.dx, p.y + v.dy)
     */
    public static Point plus(Point p, Vector v) {
        return new Point(p.x + v.dx(), p.y + v.dy());
    }
}
