package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.model.UserEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUser_whenEmailExists() {
        UserEntity user = new UserEntity();
        user.setEmail("user@email.com");
        user.setUsername("joao");
        user.setRole(UserRole.CUSTOMER);
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("user@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("user@email.com");
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenEmailNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown@email.com");
    }
}
