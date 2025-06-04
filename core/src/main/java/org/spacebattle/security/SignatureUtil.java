package org.spacebattle.security;

import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Утилита для работы с цифровыми подписями, ключами и кодированием.
 * Поддерживает:
 * - Загрузку публичного RSA-ключа из файла (формат X.509 DER, без PEM-заголовков)
 * - Подпись данных приватным ключом
 * - Проверку подписи публичным ключом
 * - Кодирование/декодирование Base64
 */
public class SignatureUtil {

    /**
     * Загружает публичный ключ из файла.
     * Ожидается X.509 формат (DER), без PEM-заголовков.
     *
     * @param file файл с публичным ключом
     * @return PublicKey объект
     * @throws Exception если файл не найден, некорректный формат или ошибка парсинга
     */
    public static PublicKey loadPublicKey(File file) throws Exception {
        byte[] keyBytes = Files.readAllBytes(file.toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    /**
     * Проверяет цифровую подпись с использованием RSA и SHA256.
     *
     * @param data      исходные данные
     * @param signature подпись
     * @param publicKey публичный ключ
     * @return true если подпись действительна, иначе false
     * @throws Exception при ошибках инициализации или валидации
     */
    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    /**
     * Подписывает данные приватным ключом, используя RSA + SHA256.
     *
     * @param data       данные
     * @param privateKey приватный ключ
     * @return байты подписи
     * @throws Exception при ошибке подписи
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    /**
     * Кодирует массив байт в строку Base64.
     */
    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Декодирует строку Base64 обратно в байты.
     */
    public static byte[] decodeBase64(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }
}
