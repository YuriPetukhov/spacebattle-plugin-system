package org.spacebattle;

import org.spacebattle.commands.Command;
import org.spacebattle.dsl.DSLCommandFactory;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

/**
 * Фабрика для создания команды JsonDSLCommandRunner.
 * Используется как точка интеграции DSL-файлов с IoC-контейнером.
 */
public class JsonDSLCommandFactory implements DSLCommandFactory {
    private IoC ioc;

    /**
     * Конструктор по умолчанию. IoC внедряется позже через setIoC().
     */
    public JsonDSLCommandFactory() {
    }

    /**
     * Внедрение IoC-контейнера для последующего использования при создании команды.
     * @param ioc внедряемый контейнер зависимостей
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
    }

    /**
     * Создаёт команду JsonDSLCommandRunner, передавая ей IoC-контейнер.
     * Объект target и DTO игнорируются, так как команда сама читает DSL-файл.
     * @param target объект (не используется)
     * @param dto DTO (не используется)
     * @return новая команда JsonDSLCommandRunner
     */
    @Override
    public Command create(IUObject target, CommandDTO dto) {
        return new JsonDSLCommandRunner(ioc);
    }

    public IoC getIoc() {
        return this.ioc;
    }

}