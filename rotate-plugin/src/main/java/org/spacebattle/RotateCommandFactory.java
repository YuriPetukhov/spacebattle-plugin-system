package org.spacebattle;

import org.spacebattle.adapter.AdapterFactory;
import org.spacebattle.behavior.RotatingObject;
import org.spacebattle.commands.Command;
import org.spacebattle.commands.CommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.entity.Angle;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.Map;

/**
 * Фабрика команд {@link RotateCommand}, отвечающая за создание команд поворота объектов.
 * <p>
 * Использует {@link AdapterFactory} для адаптации {@link IUObject} к интерфейсу {@link RotatingObject}.
 * </p>
 */
public class RotateCommandFactory implements CommandFactory {

    private AdapterFactory adapterFactory;

    /**
     * Возвращает имя действия, обрабатываемого этой фабрикой.
     *
     * @return строка "rotate"
     */
    @Override
    public String getActionName() {
        return "rotate";
    }

    /**
     * Инициализирует фабрику, регистрируя зависимости и создавая {@link AdapterFactory}.
     *
     * @param ioc IoC-контейнер, используемый для создания адаптеров и регистрации зависимостей.
     */
    public void setIoC(IoC ioc) {
        this.adapterFactory = new AdapterFactory(ioc);
        RotatingObjectIoCRegistrar.register(ioc);
    }

    /**
     * Создаёт команду {@link RotateCommand} на основе входного объекта и параметров команды.
     *
     * @param target объект, к которому применяется команда; будет адаптирован к {@link RotatingObject}.
     * @param dto    DTO с параметрами команды. Ожидается параметр "angle" (в градусах, целое число).
     * @return команда поворота {@link RotateCommand}.
     */
    @Override
    public Command create(IUObject target, CommandDTO dto) {
        Map<String, Object> params = dto.params();
        int angleValue = (int) params.getOrDefault("angle", 90); // значение по умолчанию — 90°
        Angle angle = new Angle(angleValue);

        RotatingObject ro = adapterFactory.createAdapter(RotatingObject.class, target);
        return new RotateCommand(ro, angle);
    }
}
