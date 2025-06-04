package org.spacebattle;

import org.spacebattle.adapter.AdapterFactory;
import org.spacebattle.behavior.MovingObject;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.entity.Vector;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.Map;
/**
 * Фабрика для создания команды движения "move".
 * Устанавливает в объект вектор скорости из параметров и создает MoveCommand.
 */
public class MoveCommandFactory implements CommandFactory {

    private AdapterFactory adapterFactory;

    public MoveCommandFactory() {
    }

    @Override
    public String getActionName() {
        return "move";
    }

    /**
     * Устанавливает IoC и инициализирует AdapterFactory и регистрацию зависимостей для MovingObject.
     *
     * @param ioc контейнер внедрения зависимостей
     */
    public void setIoC(IoC ioc) {
        this.adapterFactory = new AdapterFactory(ioc);
        MovingObjectIoCRegistrar.register(ioc);
    }
    /**
     * Создаёт команду движения на основе параметров DTO.
     * Поддерживаются: dx/dy или velocity как объект/карта.
     *
     * @param target объект, над которым выполняется команда
     * @param dto параметры команды
     * @return команда {@link Command}
     */
    @Override
    public Command create(IUObject target, CommandDTO dto) {
        try {
            Map<String, Object> params = dto.params();
            if (params != null) {
                if (params.containsKey("dx") && params.containsKey("dy")) {
                    int dx = toInt(params.get("dx"));
                    int dy = toInt(params.get("dy"));
                    target.setProperty("velocity", new Vector(dx, dy));
                }


                if (params.containsKey("velocity")) {
                    Object vel = params.get("velocity");
                    if (vel instanceof Vector) {
                        target.setProperty("velocity", vel);
                    } else if (vel instanceof Map<?, ?> raw) {
                        Object dx = raw.get("dx");
                        Object dy = raw.get("dy");
                        if (dx instanceof Number && dy instanceof Number) {
                            Vector vector = new Vector(((Number) dx).intValue(), ((Number) dy).intValue());
                            target.setProperty("velocity", vector);
                        } else {
                            throw new IllegalArgumentException("Invalid velocity map: " + raw);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid velocity format: " + vel);
                    }
                }
            }

            Class<?> clazz = getClass().getClassLoader().loadClass("org.spacebattle.MoveCommand");
            MovingObject mo = adapterFactory.createAdapter(MovingObject.class, target);
            if (mo == null) {
                throw new IllegalStateException("Failed to create MovingObject adapter");
            }

            System.out.println("Adapter → " + mo.getClass().getName());

            return (Command) clazz.getConstructor(MovingObject.class).newInstance(mo);

        } catch (Exception e) {
            System.err.println("Ошибка создания команды move: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to instantiate MoveCommand", e);
        }
    }

    /**
     * Преобразует объект в int. Поддерживает Number и String.
     *
     * @param obj значение
     * @return целочисленное представление
     * @throws IllegalArgumentException если формат неподдерживаемый
     */
    private int toInt(Object obj) {
        if (obj instanceof Number num) return num.intValue();
        if (obj instanceof String str) return Integer.parseInt(str);
        throw new IllegalArgumentException("Cannot convert to int: " + obj);
    }
}
