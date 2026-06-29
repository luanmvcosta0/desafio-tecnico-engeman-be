package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.auth.config.SecurityConfig;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserLoginRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserRegisterRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserLoginResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserRegisterResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service.AuthService;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // ── POST /auth/register ────────────────────────────────────────────────────

    @Test
    void register_shouldReturn201AndBody_whenValidRequest() throws Exception {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "joao@email.com", "password123", UserRole.BROKER);
        when(authService.register(any())).thenReturn(new UserRegisterResponseDto("joao", "joao@email.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("joao"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void register_shouldReturn400_whenUsernameIsBlank() throws Exception {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("", "joao@email.com", "password123", UserRole.BROKER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "not-an-email", "password123", UserRole.BROKER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn400_whenEmailIsBlank() throws Exception {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "", "password123", UserRole.BROKER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn400_whenPasswordIsTooShort() throws Exception {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "joao@email.com", "short", UserRole.BROKER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "joao@email.com", "password123", UserRole.BROKER);
        when(authService.register(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    // ── POST /auth/login ───────────────────────────────────────────────────────

    @Test
    void login_shouldReturn200AndToken_whenValidCredentials() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("joao@email.com", "password123");
        when(authService.login(any())).thenReturn(new UserLoginResponseDto("jwt-token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturn400_whenEmailIsBlank() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn400_whenEmailIsInvalid() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("not-an-email", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn400_whenPasswordIsBlank() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("joao@email.com", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
