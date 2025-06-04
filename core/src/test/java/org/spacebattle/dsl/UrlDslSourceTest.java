package org.spacebattle.dsl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.junit.jupiter.api.Assertions.*;

class UrlDslSourceTest {

    @Test
    void testOpenStreamReturnsCorrectInputStream() throws Exception {
        String content = "test: value";
        URL testUrl = new URL("test", null, -1, "/dummy.yaml", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) {
                return new URLConnection(u) {
                    @Override
                    public void connect() {}

                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(content.getBytes());
                    }
                };
            }
        });

        UrlDslSource source = new UrlDslSource(testUrl);
        try (InputStream in = source.openStream()) {
            assertNotNull(in);
            byte[] buffer = new byte[content.length()];
            int read = in.read(buffer);
            assertEquals(content.length(), read);
            assertEquals(content, new String(buffer));
        }
    }
}
