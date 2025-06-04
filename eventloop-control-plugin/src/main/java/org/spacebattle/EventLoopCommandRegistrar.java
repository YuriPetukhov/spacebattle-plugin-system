package org.spacebattle;

import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoC;
import org.spacebattle.ioc.Registrar;

import java.util.function.Function;

/**
 * Регистрирует команды управления EventLoop (жёсткая и мягкая остановка) в IoC-контейнере.
 *
 * <ul>
 *     <li><b>command:hardStop</b> — немедленная остановка EventLoop</li>
 *     <li><b>command:softStop</b> — мягкая остановка после выполнения текущих команд</li>
 * </ul>
 */
public class EventLoopCommandRegistrar implements Registrar {

    /**
     * Регистрирует команды остановки EventLoop в IoC-контейнер.
     *
     * @param ioc IoC-контейнер, куда регистрируются команды
     */
    @Override
    public void register(IoC ioc) {
        EventLoop eventLoop = (EventLoop) ioc.resolve("eventLoop");

        ioc.resolve("IoC.Register", "command:hardStop",
                (Function<Object[], Object>) args -> new HardStopCommand(eventLoop));

        ioc.resolve("IoC.Register", "command:softStop",
                (Function<Object[], Object>) args -> new SoftStopCommand(eventLoop));
    }
}
