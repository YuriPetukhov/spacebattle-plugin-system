package org.spacebattle.entity;

/**
 * Вектор смещения с неотрицательными координатами.
 * Используется для задания скорости или направления движения.
 *
 * Ограничение: отрицательные значения dx и dy запрещены.
 *
 * Пример:
 * new Vector(1, 2) — движение вправо и вниз.
 */
public record Vector(int dx, int dy) {
    /**
     * Создаёт вектор. Значения dx и dy должны быть неотрицательны.
     *
     * @throws IllegalArgumentException если dx < 0 или dy < 0
     */
    public Vector {
        if (dx < 0 || dy < 0)
            throw new IllegalArgumentException("Velocity cannot be negative");
    }
}
