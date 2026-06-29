package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.repository;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        UserEntity user = new UserEntity();
        user.setUsername("joao");
        user.setEmail("joao@email.com");
        user.setPassword("encoded-password");
        user.setRole(UserRole.CUSTOMER);
        entityManager.persistAndFlush(user);

        Optional<UserEntity> result = userRepository.findByEmail("joao@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("joao@email.com");
        assertThat(result.get().getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<UserEntity> result = userRepository.findByEmail("naoexiste@email.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnCorrectUser_whenMultipleUsersExist() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("joao");
        user1.setEmail("joao@email.com");
        user1.setPassword("pass1");
        user1.setRole(UserRole.CUSTOMER);

        UserEntity user2 = new UserEntity();
        user2.setUsername("maria");
        user2.setEmail("maria@email.com");
        user2.setPassword("pass2");
        user2.setRole(UserRole.BROKER);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Optional<UserEntity> result = userRepository.findByEmail("maria@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("maria@email.com");
        assertThat(result.get().getRole()).isEqualTo(UserRole.BROKER);
    }
}
