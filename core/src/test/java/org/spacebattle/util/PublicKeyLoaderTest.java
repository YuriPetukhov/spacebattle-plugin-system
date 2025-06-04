package org.spacebattle.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

class PublicKeyLoaderTest {

    @Test
    void testLoadFromValidPem() throws Exception {
        // Сгенерированный публичный ключ RSA (2048 bit) в PEM-формате
        String pem = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApXGpAx74TX4ci/UvvV4O
                J4B2MeWBtr3E4sjVXgMCgSRoLUtCmO8mrcoc0HV6+gV2UJIN4ep5dSE/jd0xVPxi
                bNMVR7aUYI5JUlKHzvcAPrCNsUlUfnRhzsVLdld/umGPMJz8XLck4hMfB6JIb8ZT
                SYVDs3l47Tql2JGIoiyM8uqFqOCY7Se4Imq1+feyyGHX7l2Cq+SS3GOsyeuG6A6F
                O0TGmm8O1XHOEoRcvVURm9G6qOYUIenVDO7KDjH/2quIqZ1HY2gRfrRtTVnLQSLx
                dKPm9MEdTfw81+yk5P2lzN+nukn3WeidJvN1AVUZxilx7+R45EKpgkJ3YtqRZoNf
                wQIDAQAB
                -----END PUBLIC KEY-----
                """;

        Path temp = Files.createTempFile("public-key", ".pem");
        Files.writeString(temp, pem);

        PublicKey key = PublicKeyLoader.loadFromPem(temp);

        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
        assertEquals("X.509", key.getFormat());
    }

    @Test
    void testInvalidKeyShouldThrowException() throws Exception {
        Path temp = Files.createTempFile("bad-key", ".pem");
        Files.writeString(temp, "-----BEGIN PUBLIC KEY-----\nabc\n-----END PUBLIC KEY-----");

        assertThrows(Exception.class, () -> {
            PublicKeyLoader.loadFromPem(temp);
        });
    }

    @Test
    void testMissingFileShouldThrowException() {
        Path nonExistent = Path.of("no-such-file.pem");

        assertThrows(Exception.class, () -> {
            PublicKeyLoader.loadFromPem(nonExistent);
        });
    }
}
