package org.spacebattle.dsl;

/**
 * Обобщённый интерфейс загрузчика DSL-описаний.
 * <p>
 * Позволяет загружать данные произвольного типа {@code T} из источника {@link DslSource},
 * который может быть реализован для чтения из файла, URL, строки и т.д.
 * </p>
 *
 * @param <T> тип результата загрузки (например, {@code Map}, {@code ObjectDefinition}, {@code List<CommandDTO>})
 */
public interface DslLoader<T> {

    /**
     * Загружает данные из указанного DSL-источника.
     *
     * @param source абстрактный источник данных (файл, URL и т.д.)
     * @return результат загрузки, тип зависит от реализации
     * @throws Exception если произошла ошибка чтения или парсинга
     */
    T load(DslSource source) throws Exception;
}
