package org.spacebattle.security;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;

class SignatureUtilTest {

    @Test
    void testSignAndVerify_success() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        byte[] data = "test-data".getBytes();
        byte[] signature = SignatureUtil.sign(data, pair.getPrivate());

        boolean verified = SignatureUtil.verifySignature(data, signature, pair.getPublic());
        assertTrue(verified, "Подпись должна быть валидной");
    }

    @Test
    void testVerifySignature_invalid_shouldFail() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        byte[] data = "valid-data".getBytes();
        byte[] tampered = "tampered-data".getBytes();

        byte[] signature = SignatureUtil.sign(data, pair.getPrivate());

        boolean verified = SignatureUtil.verifySignature(tampered, signature, pair.getPublic());
        assertFalse(verified, "Подпись должна быть недействительной");
    }

    @Test
    void testBase64EncodingDecoding() {
        byte[] original = "binary-data".getBytes();
        String encoded = SignatureUtil.encodeBase64(original);
        byte[] decoded = SignatureUtil.decodeBase64(encoded);

        assertArrayEquals(original, decoded, "Base64 декодирование должно вернуть исходные данные");
    }

    @Test
    void testLoadPublicKey_success() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        // сохраняем публичный ключ во временный файл
        byte[] encodedKey = pair.getPublic().getEncoded();
        File keyFile = File.createTempFile("pubkey", ".key");
        Files.write(keyFile.toPath(), encodedKey);

        PublicKey loaded = SignatureUtil.loadPublicKey(keyFile);
        assertArrayEquals(pair.getPublic().getEncoded(), loaded.getEncoded(), "Загруженный ключ должен совпадать");
    }
}
