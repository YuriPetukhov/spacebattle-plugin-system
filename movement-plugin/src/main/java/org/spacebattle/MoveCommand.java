package org.spacebattle;

import org.spacebattle.behavior.MovingObject;
import org.spacebattle.commands.Command;
import org.spacebattle.entity.Point;

/**
 * Команда перемещения объекта, реализующего {@link MovingObject}.
 * <p>
 * Вычисляет новую позицию объекта как сумму текущей позиции и вектора скорости,
 * и обновляет координаты объекта.
 * </p>
 * <p>
 * Конструктор без аргументов используется для отладки и не должен применяться в боевом коде.
 * </p>
 */
public class MoveCommand implements Command {
    private final MovingObject object;

    /**
     * Создаёт команду перемещения для заданного объекта.
     *
     * @param object объект, реализующий {@link MovingObject}, для которого будет выполняться перемещение
     */
    public MoveCommand(MovingObject object) {
        this.object = object;
    }

    /**
     * Конструктор без объекта — используется для отладки.
     * Не выполняет никаких действий при вызове {@link #execute()}, если объект не установлен.
     */
    public MoveCommand() {
        this.object = null;
        System.out.println("MoveCommand создан без объекта — только для отладки!");
    }

    /**
     * Выполняет перемещение объекта:
     * новая позиция = текущая позиция + скорость.
     */
    @Override
    public void execute() {
        Point newLocation = Point.plus(object.getLocation(), object.getVelocity());
        object.setLocation(newLocation);
    }
}
