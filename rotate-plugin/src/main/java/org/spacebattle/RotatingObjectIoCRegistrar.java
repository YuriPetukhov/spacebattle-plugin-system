package org.spacebattle;

import org.spacebattle.entity.Angle;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.function.Function;

/**
 * Регистрирует функции получения и установки угловых свойств объектов в IoC-контейнер.
 * <p>
 * После регистрации можно обращаться к свойствам объектов через IoC-контейнер по ключам:
 * <ul>
 *     <li>"RotatingObject:getAngle" — возвращает {@link Angle}, заданный в свойстве "angle".</li>
 *     <li>"RotatingObject:setAngle" — устанавливает {@link Angle} в свойство "angle".</li>
 *     <li>"RotatingObject:getAngularVelocity" — возвращает {@link Angle} из свойства "angularVelocity".</li>
 * </ul>
 * </p>
 * Используется для адаптации {@link IUObject} к интерфейсу {@link org.spacebattle.behavior.RotatingObject}
 * через динамическое разрешение поведения.
 */
public class RotatingObjectIoCRegistrar {

    /**
     * Выполняет регистрацию функций для получения и установки угла и угловой скорости
     * объекта в IoC-контейнер.
     *
     * @param ioc IoC-контейнер, в который регистрируются функции.
     */
    public static void register(IoC ioc) {
        // Регистрирует функцию получения угла
        ioc.resolve("IoC.Register", "RotatingObject:getAngle",
                (Function<Object[], Object>) args -> ((IUObject) args[0])
                        .<Angle>getProperty("angle")
                        .orElseThrow(() -> new IllegalStateException("Angle not set")));

        // Регистрирует функцию установки угла
        ioc.resolve("IoC.Register", "RotatingObject:setAngle",
                (Function<Object[], Object>) args -> {
                    IUObject obj = (IUObject) args[0];
                    Angle angle = (Angle) args[1];
                    obj.setProperty("angle", angle);
                    return null;
                });

        // Регистрирует функцию получения угловой скорости
        ioc.resolve("IoC.Register", "RotatingObject:getAngularVelocity",
                (Function<Object[], Object>) args -> ((IUObject) args[0])
                        .<Angle>getProperty("angularVelocity")
                        .orElseThrow(() -> new IllegalStateException("Angular velocity not set")));
    }
}
