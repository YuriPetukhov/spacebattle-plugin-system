package org.spacebattle.security;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class SignaturePluginValidatorTest {

    /**
     * Создаёт временный JAR-файл с заданным содержимым и подписывает его.
     */
    private File createSignedJar(PublicKey publicKey, PrivateKey privateKey, boolean correctSignature) throws Exception {
        File jarFile = File.createTempFile("plugin", ".jar");
        jarFile.deleteOnExit();

        byte[] yamlContent = "name: test-plugin".getBytes();

        // Подписываем plugin.yaml
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(yamlContent);
        byte[] signatureBytes = signature.sign();

        if (!correctSignature) {
            signatureBytes[0]++; // Искажаем подпись
        }

        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile))) {
            out.putNextEntry(new JarEntry("META-INF/plugin.yaml"));
            out.write(yamlContent);
            out.closeEntry();

            out.putNextEntry(new JarEntry("META-INF/plugin.sig"));
            out.write(signatureBytes);
            out.closeEntry();
        }

        return jarFile;
    }

    @Test
    void testValidate_withCorrectSignature_shouldPass() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();

        File jar = createSignedJar(pair.getPublic(), pair.getPrivate(), true);

        PluginValidator validator = new SignaturePluginValidator(pair.getPublic());
        assertDoesNotThrow(() -> validator.validate(jar));
    }

    @Test
    void testValidate_withIncorrectSignature_shouldThrow() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();

        File jar = createSignedJar(pair.getPublic(), pair.getPrivate(), false);

        PluginValidator validator = new SignaturePluginValidator(pair.getPublic());
        assertThrows(SecurityException.class, () -> validator.validate(jar));
    }

    @Test
    void testValidate_missingFiles_shouldThrow() throws Exception {
        File emptyJar = File.createTempFile("empty", ".jar");
        emptyJar.deleteOnExit();

        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(emptyJar))) {
            out.putNextEntry(new JarEntry("some-other-file.txt"));
            out.write("hello".getBytes());
            out.closeEntry();
        }

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        PublicKey publicKey = gen.generateKeyPair().getPublic();

        PluginValidator validator = new SignaturePluginValidator(publicKey);
        assertThrows(SecurityException.class, () -> validator.validate(emptyJar));
    }
}
