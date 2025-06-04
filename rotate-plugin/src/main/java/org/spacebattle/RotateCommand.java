package org.spacebattle;

import org.spacebattle.behavior.RotatingObject;
import org.spacebattle.commands.Command;
import org.spacebattle.entity.Angle;

/**
 * Команда вращения объекта на заданный угол.
 * <p>
 * Используется для изменения текущего угла поворота объекта, реализующего интерфейс {@link RotatingObject}.
 * Команда добавляет заданный угол к текущему и устанавливает результат как новый угол объекта.
 * </p>
 */
public class RotateCommand implements Command {
    private final RotatingObject object;
    private final Angle angle;

    /**
     * Создаёт новую команду вращения.
     *
     * @param object объект, который должен быть повёрнут. Должен реализовывать {@link RotatingObject}.
     * @param angle  угол, на который нужно повернуть объект. Может быть положительным или отрицательным.
     */
    public RotateCommand(RotatingObject object, Angle angle) {
        this.object = object;
        this.angle = angle;
    }

    /**
     * Выполняет команду: изменяет угол объекта, добавляя к текущему углу заданный.
     */
    @Override
    public void execute() {
        Angle current = object.getAngle();
        Angle result = Angle.add(current, angle);
        object.setAngle(result);
        System.out.println("New angle: " + result);
    }
}
