package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spacebattle.TokenVerifierClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenVerifierClientTest {

    private HttpClient mockHttpClient;
    private HttpResponse<Void> mockResponse;
    private TokenVerifierClient client;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        client = new TokenVerifierClient("http://verifier-url");
    }

    @Test
    void verify_shouldReturnTrue_whenStatusCodeIs200() throws Exception {
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<Void> mockResponse = mock(HttpResponse.class);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);

        TokenVerifierClient client = new TokenVerifierClient("http://verifier-url", mockHttpClient);
        boolean result = client.verify("token");

        assertTrue(result);
    }

    @Test
    void verify_shouldReturnFalse_whenStatusCodeIsNot200() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(401);

        boolean result = client.verify("invalid-token");

        assertFalse(result);
    }

    @Test
    void verify_shouldReturnFalse_whenExceptionOccurs() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Connection error"));

        boolean result = client.verify("any-token");

        assertFalse(result);
    }
}
