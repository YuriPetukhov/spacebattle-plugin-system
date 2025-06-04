package org.spacebattle.behavior;

import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;

/**
 * Интерфейс объекта, обладающего положением и скоростью.
 * Используется для абстракции над объектами, способными перемещаться.
 */
public interface MovingObject {

    /**
     * Возвращает текущую позицию объекта.
     */
    Point getLocation();

    /**
     * Возвращает текущую скорость (вектор смещения).
     */
    Vector getVelocity();

    /**
     * Устанавливает новую позицию объекта.
     */
    void setLocation(Point newLocation);
}
