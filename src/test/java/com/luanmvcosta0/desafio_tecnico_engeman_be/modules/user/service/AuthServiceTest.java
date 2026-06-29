package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserLoginRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserRegisterRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserLoginResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserRegisterResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.model.UserEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldAssignAdminRole_whenFirstUserInSystem() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("admin", "admin@email.com", "password123", UserRole.BROKER);
        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        authService.register(dto);

        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void register_shouldAssignBrokerRole_whenRoleIsBrokerAndNotFirstUser() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("broker", "broker@email.com", "password123", UserRole.BROKER);
        when(userRepository.findByEmail("broker@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        authService.register(dto);

        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.BROKER);
    }

    @Test
    void register_shouldAssignCustomerRole_whenRoleIsNotBrokerAndNotFirstUser() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("customer", "customer@email.com", "password123", UserRole.CUSTOMER);
        when(userRepository.findByEmail("customer@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(2L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        authService.register(dto);

        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void register_shouldAssignCustomerRole_whenRoleIsAdminAndNotFirstUser() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("user", "user@email.com", "password123", UserRole.ADMIN);
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(3L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        authService.register(dto);

        assertThat(captor.getValue().getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void register_shouldEncodePassword_beforeSaving() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("user", "user@email.com", "plaintext", UserRole.BROKER);
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed-password");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        authService.register(dto);

        assertThat(captor.getValue().getPassword()).isEqualTo("hashed-password");
    }

    @Test
    void register_shouldReturnUsernameAndEmail_onSuccess() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "joao@email.com", "password123", UserRole.BROKER);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRegisterResponseDto result = authService.register(dto);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("joao@email.com");
    }

    @Test
    void register_shouldThrowConflict_whenEmailAlreadyExists() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("user", "exists@email.com", "password123", null);
        when(userRepository.findByEmail("exists@email.com")).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
    }

    @Test
    void register_shouldNotSaveUser_whenEmailAlreadyExists() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("user", "exists@email.com", "password123", null);
        when(userRepository.findByEmail("exists@email.com")).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ResponseStatusException.class);

        verify(userRepository).findByEmail("exists@email.com");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        UserLoginRequestDto dto = new UserLoginRequestDto("user@email.com", "password123");
        UserEntity user = new UserEntity();
        user.setEmail("user@email.com");
        user.setRole(UserRole.CUSTOMER);
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        UserLoginResponseDto result = authService.login(dto);

        assertThat(result.token()).isEqualTo("jwt-token");
    }

    @Test
    void login_shouldAuthenticateWithEmailAndPassword() {
        UserLoginRequestDto dto = new UserLoginRequestDto("user@email.com", "password123");
        UserEntity user = new UserEntity();
        user.setEmail("user@email.com");
        user.setRole(UserRole.BROKER);
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        when(authenticationManager.authenticate(captor.capture()))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(jwtService.generateToken(user)).thenReturn("token");

        authService.login(dto);

        assertThat(captor.getValue().getPrincipal()).isEqualTo("user@email.com");
        assertThat(captor.getValue().getCredentials()).isEqualTo("password123");
    }

    @Test
    void register_shouldReturnUsernameAndEmail_inResponseDto() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("joao", "joao@email.com", "password123", UserRole.BROKER);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRegisterResponseDto result = authService.register(dto);

        assertThat(result.username()).isEqualTo("joao");
        assertThat(result.email()).isEqualTo("joao@email.com");
    }

    @Test
    void login_shouldPropagateException_whenAuthenticationFails() {
        UserLoginRequestDto dto = new UserLoginRequestDto("user@email.com", "wrongpassword");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BadCredentialsException.class);
    }
}
