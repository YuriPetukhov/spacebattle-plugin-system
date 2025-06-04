package org.spacebattle;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthClientTest {

    private MockWebServer mockServer;

    @BeforeEach
    void startServer() throws Exception {
        mockServer = new MockWebServer();
        mockServer.start(8081);
    }

    @AfterEach
    void stopServer() throws Exception {
        mockServer.shutdown();
    }

    @Test
    void testAuthenticateSetsTokenProperty() throws Exception {
        String expectedToken = "abc123";

        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"token\":\"" + expectedToken + "\"}")
                .addHeader("Content-Type", "application/json"));

        AuthClient.authenticate("http://localhost:8081/login");

        String actualToken = System.getProperty("auth.token");
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testAuthenticateHandlesErrorCode() throws Exception {
        mockServer.enqueue(new MockResponse().setResponseCode(401));

        AuthClient.authenticate("http://localhost:8081/login");

        assertNull(System.getProperty("auth.token"));
    }
}
