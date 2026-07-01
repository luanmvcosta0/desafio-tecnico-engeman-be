package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.repository;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyEntity, String>, JpaSpecificationExecutor<PropertyEntity> {

    Optional<PropertyEntity> findByNameContainingIgnoreCase(String name);

}
