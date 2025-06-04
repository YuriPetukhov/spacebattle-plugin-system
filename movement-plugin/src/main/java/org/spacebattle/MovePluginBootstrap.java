package org.spacebattle;

import org.spacebattle.ioc.IoC;

/**
 * Bootstrap-класс плагина, который регистрирует фабрику команды "move" в IoC-контейнере.
 * Вызывается при загрузке плагина, чтобы внедрить команду движения в систему.
 */
public class MovePluginBootstrap {
    private IoC ioc;

    /**
     * Устанавливает IoC-контейнер, который будет использоваться для регистрации команды.
     * @param ioc контейнер внедрения зависимостей
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Выполняет регистрацию команды "move" в IoC-контейнере.
     * Также создаёт фабрику MoveCommandFactory и передаёт ей IoC.
     */
    public void run() {
        System.out.println("MovePluginBootstrap запускается");

        MoveCommandFactory factory = new MoveCommandFactory();
        factory.setIoC(ioc); // регистрация зависимостей команды

        ioc.register("command:move", args -> factory);
        System.out.println("Команда 'move' зарегистрирована");
    }
}
