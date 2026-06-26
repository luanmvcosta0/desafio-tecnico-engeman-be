package com.luanmvcosta0.desafio_tecnico_engeman_be.repository;

import com.luanmvcosta0.desafio_tecnico_engeman_be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
