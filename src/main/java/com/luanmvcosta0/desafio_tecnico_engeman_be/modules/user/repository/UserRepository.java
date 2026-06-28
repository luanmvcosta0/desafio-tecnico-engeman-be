package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.repository;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

}
