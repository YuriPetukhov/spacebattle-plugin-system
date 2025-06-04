package org.spacebattle.security;

import java.io.File;
import java.security.PublicKey;
import java.security.Signature;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Валидатор плагинов, проверяющий цифровую подпись plugin.yaml,
 * используя файл подписи plugin.sig и открытый ключ.
 * <p>
 * Ожидается, что внутри JAR-файла находятся две записи:
 * - META-INF/plugin.yaml — исходный файл описания плагина
 * - META-INF/plugin.sig  — цифровая подпись файла plugin.yaml
 * <p>
 * Подпись проверяется с использованием алгоритма SHA256withRSA.
 */
public class SignaturePluginValidator implements PluginValidator {

    private final PublicKey publicKey;

    /**
     * Создаёт валидатор с переданным открытым ключом.
     *
     * @param publicKey открытый ключ для верификации подписи
     */
    public SignaturePluginValidator(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Проверяет JAR-файл плагина на наличие корректной цифровой подписи plugin.yaml.
     *
     * @param jarFile JAR-файл с плагином
     * @throws SecurityException если подпись отсутствует, недействительна
     *                           или возникает ошибка при проверке
     */
    @Override
    public void validate(File jarFile) throws SecurityException {
        try {
            JarFile jar = new JarFile(jarFile);

            // Извлечение plugin.yaml
            JarEntry entry = jar.getJarEntry("META-INF/plugin.yaml");
            if (entry == null) {
                throw new SecurityException("plugin.yaml not found in jar");
            }
            byte[] yamlData = jar.getInputStream(entry).readAllBytes();

            // Извлечение plugin.sig
            JarEntry sigEntry = jar.getJarEntry("META-INF/plugin.sig");
            if (sigEntry == null) {
                throw new SecurityException("plugin.sig not found in jar");
            }
            byte[] signatureBytes = jar.getInputStream(sigEntry).readAllBytes();

            // Проверка подписи
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(yamlData);

            boolean verified = sig.verify(signatureBytes);
            if (!verified) {
                throw new SecurityException("Signature verification failed");
            }

        } catch (Exception e) {
            throw new SecurityException("Plugin validation failed", e);
        }
    }
}
