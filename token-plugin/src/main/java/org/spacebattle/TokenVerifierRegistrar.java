package org.spacebattle;

import org.spacebattle.ioc.IoC;

import java.util.function.Function;

/**
 * Класс {@code TokenVerifierRegistrar} отвечает за регистрацию функции верификации токенов
 * в IoC-контейнере под именем {@code "token-verifier"}.
 *
 * <p>Зарегистрированная функция принимает массив аргументов {@code Object[]},
 * где первый элемент должен быть строкой с токеном, и возвращает {@code true}, если
 * токен валиден, и {@code false} — в противном случае.</p>
 */
public class TokenVerifierRegistrar {

    /**
     * Контейнер внедрения зависимостей, в который регистрируется функция.
     */
    private final IoC ioc;

    /**
     * Клиент, осуществляющий фактическую верификацию токенов.
     */
    private final TokenVerifierClient client;

    /**
     * Создаёт новый экземпляр {@code TokenVerifierRegistrar}.
     *
     * @param ioc    IoC-контейнер, в который будет зарегистрирована функция
     * @param client клиент для выполнения HTTP-запросов верификации токенов
     */
    public TokenVerifierRegistrar(IoC ioc, TokenVerifierClient client) {
        this.ioc = ioc;
        this.client = client;
    }

    /**
     * Регистрирует функцию верификации токенов в IoC-контейнере.
     *
     * <p>Функция регистрируется под ключом {@code "token-verifier"} и реализует
     * интерфейс {@code Function<Object[], Object>}, ожидая один аргумент типа {@code String}
     * — токен, подлежащий проверке. Возвращаемое значение — результат вызова
     * {@link TokenVerifierClient#verify(String)}.</p>
     */
    public void register() {
        Function<Object[], Object> function = args -> client.verify((String) args[0]);
        ioc.resolve("IoC.Register", "token-verifier", function);
        System.out.println("Token verifier зарегистрирован в IoC");
    }
}
