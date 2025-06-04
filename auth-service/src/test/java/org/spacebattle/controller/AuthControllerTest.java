package org.spacebattle.controller;

import org.junit.jupiter.api.Test;
import org.spacebattle.service.ITokenService;
import org.spacebattle.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private ITokenService tokenService;

    @Test
    void register_shouldReturnOk_whenUserDoesNotExist() throws Exception {
        when(userService.exists("alice")).thenReturn(false);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"alice\", \"password\": \"123\"}"))
                .andExpect(status().isOk());

        verify(userService).register("alice", "123");
    }

    @Test
    void register_shouldReturnConflict_whenUserExists() throws Exception {
        when(userService.exists("alice")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"alice\", \"password\": \"123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() throws Exception {
        when(userService.validate("bob", "pass")).thenReturn(true);
        when(tokenService.generateToken("bob")).thenReturn("abc.def.ghi");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"bob\", \"password\": \"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc.def.ghi"));
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsInvalid() throws Exception {
        when(userService.validate("bob", "wrong")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"bob\", \"password\": \"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verify_shouldReturnOk_whenTokenIsValid() throws Exception {
        when(tokenService.isValid("valid-token")).thenReturn(true);

        mockMvc.perform(get("/auth/verify")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }

    @Test
    void verify_shouldReturnUnauthorized_whenTokenIsInvalid() throws Exception {
        when(tokenService.isValid("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/auth/verify")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid token")));
    }
}
