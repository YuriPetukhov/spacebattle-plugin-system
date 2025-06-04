package org.spacebattle.behavior;

import org.spacebattle.entity.Angle;

/**
 * Интерфейс объекта, обладающего углом поворота.
 * Используется для абстракции над вращающимися объектами.
 */
public interface RotatingObject {

    /**
     * Возвращает текущий угол поворота объекта.
     */
    Angle getAngle();

    /**
     * Устанавливает новый угол поворота.
     */
    void setAngle(Angle newAngle);

    Angle getAngularVelocity();
}
