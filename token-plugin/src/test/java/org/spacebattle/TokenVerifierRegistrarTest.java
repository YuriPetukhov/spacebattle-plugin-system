package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.spacebattle.ioc.IoC;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TokenVerifierRegistrarTest {

    private IoC mockIoC;
    private TokenVerifierClient mockClient;
    private TokenVerifierRegistrar registrar;

    @BeforeEach
    void setUp() {
        mockIoC = mock(IoC.class);
        mockClient = mock(TokenVerifierClient.class);
        registrar = new TokenVerifierRegistrar(mockIoC, mockClient);
    }

    @Test
    void register_shouldRegisterVerifierFunctionInIoC() {
        registrar.register();

        verify(mockIoC).resolve(eq("IoC.Register"), eq("token-verifier"), any(Function.class));
    }

    @Test
    void registeredFunction_shouldDelegateToClientVerify() {

        ArgumentCaptor<Function> captor = ArgumentCaptor.forClass(Function.class);

        registrar.register();

        verify(mockIoC).resolve(eq("IoC.Register"), eq("token-verifier"), captor.capture());

        @SuppressWarnings("unchecked")
        Function<Object[], Object> registeredFunction = captor.getValue();

        when(mockClient.verify("valid-token")).thenReturn(true);

        Object result = registeredFunction.apply(new Object[]{"valid-token"});

        assertTrue((Boolean) result);
        verify(mockClient).verify("valid-token");
    }
}
