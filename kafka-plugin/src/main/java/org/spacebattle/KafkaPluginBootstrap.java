package org.spacebattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.spacebattle.dto.CommandDTO;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.ioc.IoC;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Инициализирует Kafka consumer для обработки команд из Kafka-топика.
 * <p>
 * Регистрирует в IoC:
 * - JsonToCommand: преобразование JSON в CommandDTO;
 * - CommandDispatcher: потребитель команд (по умолчанию логирует);
 * - KafkaConsumer: экземпляр KafkaCommandConsumer;
 * <p>
 * Также запускает Kafka consumer в отдельном потоке.
 */
public class KafkaPluginBootstrap {

    private IoC ioc;

    /**
     * Устанавливает IoC-контейнер для разрешения зависимостей.
     * @param ioc IoC-контейнер
     */
    public void setIoC(IoC ioc) {
        this.ioc = ioc;
        System.out.println("IoC внедрён в KafkaPluginBootstrap");
    }

    /**
     * Основной метод запуска bootstrap'а Kafka consumer.
     * Регистрирует необходимые зависимости и запускает consumer в потоке.
     */
    public void run() {
        System.out.println("KafkaPluginBootstrap.run() стартует");

        try {
            if (!ioc.contains("JsonToCommand")) {
                System.out.println("Регистрируем JsonToCommand");
                ioc.register("JsonToCommand", args -> (Function<String, Object>) json -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.readValue(json, CommandDTO.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка парсинга JSON", e);
                    }
                });
            }

            if (!ioc.contains("CommandDispatcher")) {
                System.out.println("Регистрируем CommandDispatcher");
                ioc.register("CommandDispatcher", args -> (Consumer<Object>) command -> {
                    System.out.println("Command received (default dispatcher): " + command);
                });
            }

            System.out.println("Пытаемся получить event-loop из IoC");
            EventLoop eventLoop = (EventLoop) ioc.resolve("event-loop");
            System.out.println("event-loop получен: " + eventLoop);

            System.out.println("Создаём KafkaCommandConsumer");
            KafkaCommandConsumer consumer = new KafkaCommandConsumer(eventLoop, ioc);
            System.out.println("KafkaCommandConsumer создан");

            System.out.println("Регистрируем KafkaConsumer в IoC");
            ioc.register("KafkaConsumer", args -> consumer);
            System.out.println("KafkaConsumer зарегистрирован");

            System.out.println("Запускаем Kafka consumer в новом потоке");
            new Thread(consumer).start();
            System.out.println("Kafka consumer успешно запущен");

        } catch (Exception e) {
            System.err.println("KafkaPluginBootstrap.run() завершился с ошибкой: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new RuntimeException("Kafka bootstrap failed", e);
        }
    }
}
