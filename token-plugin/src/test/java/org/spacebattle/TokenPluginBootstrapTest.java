package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spacebattle.ioc.IoC;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenPluginBootstrapTest {

    @Mock
    private IoC ioc;

    private TokenPluginBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new TokenPluginBootstrap();
        bootstrap.setIoC(ioc);
    }

    @Test
    void run_shouldInitializeWithDefaultUrl() {
        System.setProperty("token.verifier.url", ""); // Очищаем свойство

        bootstrap.run();

        verify(ioc).resolve(eq("IoC.Register"), eq("token-verifier"), any());
    }

    @Test
    void run_shouldUseCustomUrlFromSystemProperties() {
        String customUrl = "http://custom-url";
        System.setProperty("token.verifier.url", customUrl);

        bootstrap.run();

        verify(ioc).resolve(eq("IoC.Register"), eq("token-verifier"), any());
    }

    @Test
    public void testRunRegistersVerifier() {

        IoC ioc = Mockito.mock(IoC.class);
        System.setProperty("token.verifier.url", "http://mock-url");

        TokenPluginBootstrap bootstrap = new TokenPluginBootstrap();
        bootstrap.setIoC(ioc);

        bootstrap.run();

        verify(ioc).resolve(
                eq("IoC.Register"),
                eq("token-verifier"),
                any(Function.class)
        );
    }
}