package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.model.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm";
    private static final long EXPIRATION = 3600000L;

    private JwtService jwtService;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
        ReflectionTestUtils.invokeMethod(jwtService, "init");

        user = new UserEntity();
        user.setEmail("user@email.com");
        user.setRole(UserRole.CUSTOMER);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_shouldReturnJwtFormat() {
        String token = jwtService.generateToken(user);

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractEmail_shouldReturnEmailEmbeddedInToken() {
        String token = jwtService.generateToken(user);

        String extracted = jwtService.extractEmail(token);

        assertThat(extracted).isEqualTo("user@email.com");
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenMatchesUserAndIsNotExpired() {
        String token = jwtService.generateToken(user);

        boolean valid = jwtService.isTokenValid(token, user);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenBelongsToDifferentUser() {
        String token = jwtService.generateToken(user);

        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@email.com");
        otherUser.setRole(UserRole.BROKER);

        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_shouldThrowExpiredJwtException_whenTokenIsExpired() {
        JwtService expiredJwtService = new JwtService();
        ReflectionTestUtils.setField(expiredJwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(expiredJwtService, "expiration", -1000L);
        ReflectionTestUtils.invokeMethod(expiredJwtService, "init");

        String expiredToken = expiredJwtService.generateToken(user);

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, user))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void generateToken_shouldEmbedRoleClaimInToken() {
        UserEntity adminUser = new UserEntity();
        adminUser.setEmail("admin@email.com");
        adminUser.setRole(UserRole.ADMIN);

        String token = jwtService.generateToken(adminUser);
        String email = jwtService.extractEmail(token);

        assertThat(email).isEqualTo("admin@email.com");
        assertThat(jwtService.isTokenValid(token, adminUser)).isTrue();
    }

    @Test
    void generateToken_shouldEmbedCorrectRoleClaimValue() {
        UserEntity adminUser = new UserEntity();
        adminUser.setEmail("admin@email.com");
        adminUser.setRole(UserRole.ADMIN);

        String token = jwtService.generateToken(adminUser);

        String[] parts = token.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        assertThat(payloadJson).contains("\"role\":\"ADMIN\"");
    }

    @Test
    void extractEmail_shouldThrow_whenTokenIsMalformed() {
        assertThatThrownBy(() -> jwtService.extractEmail("not.valid.jwt"))
                .isInstanceOf(JwtException.class);
    }
}
