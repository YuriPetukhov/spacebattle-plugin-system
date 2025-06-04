package org.spacebattle;

import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.setup.SpaceBattleInitializer;

/**
 * Точка входа в сервер SpaceBattle.
 * Создаёт контейнер IoC и передаёт его в инициализатор, который:
 * - регистрирует обработчики и компоненты,
 * - настраивает очередь событий,
 * - загружает плагины и запускает слежение за ними.
 */

public class SpaceBattleServer {
    public static void main(String[] args) throws Exception {
            IoCContainer ioc = new IoCContainer();
            SpaceBattleInitializer.initialize(ioc);
        }
}

