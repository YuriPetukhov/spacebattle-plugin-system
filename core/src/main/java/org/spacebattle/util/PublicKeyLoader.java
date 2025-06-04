package org.spacebattle.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Утилита для загрузки публичного ключа из PEM-файла.
 */
public class PublicKeyLoader {

    /**
     * Загружает публичный RSA-ключ из PEM-файла.
     *
     * <p>Ожидается формат:
     * -----BEGIN PUBLIC KEY-----
     * base64...
     * -----END PUBLIC KEY-----
     *
     * @param path путь к PEM-файлу
     * @return объект {@link PublicKey}
     * @throws Exception при ошибке чтения файла или парсинга ключа
     */
    public static PublicKey loadFromPem(Path path) throws Exception {
        String key = Files.readString(path);

        // Удаляем заголовки и пробелы (включая переносы строк)
        String clean = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // удаляет все пробелы и переносы строк

        byte[] decoded = Base64.getDecoder().decode(clean); // декодируем base64
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded); // создаём X509-спецификацию ключа
        KeyFactory factory = KeyFactory.getInstance("RSA"); // создаём фабрику для RSA

        return factory.generatePublic(spec); // создаём объект PublicKey
    }
}
