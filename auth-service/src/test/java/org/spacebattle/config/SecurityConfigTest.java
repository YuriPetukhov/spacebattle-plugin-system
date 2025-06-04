package org.spacebattle.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void passwordEncoderBeanExists_andEncrypts() {
        assertNotNull(passwordEncoder);
        String encoded = passwordEncoder.encode("secret");
        assertTrue(passwordEncoder.matches("secret", encoded));
    }

    @Test
    void accessToAuthLoginEndpointWithoutAuthShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                {
                    "username": "unknown",
                    "password": "wrong"
                }
            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessToNonAuthEndpointIsDenied() throws Exception {
        mockMvc.perform(get("/some/other"))
                .andExpect(status().isForbidden());
    }
}
