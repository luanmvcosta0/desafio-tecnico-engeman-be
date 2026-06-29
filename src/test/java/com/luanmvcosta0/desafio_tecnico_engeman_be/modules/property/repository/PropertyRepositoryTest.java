package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.repository;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.enums.Type;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PropertyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PropertyRepository propertyRepository;

    @Test
    void findByNameContainingIgnoreCase_shouldReturnProperty_whenNameMatches() {
        PropertyEntity property = new PropertyEntity();
        property.setName("Casa Bonita");
        property.setRooms(3);
        property.setPrice(new BigDecimal("250000.00"));
        property.setType(Type.HOUSE);
        entityManager.persistAndFlush(property);

        Optional<PropertyEntity> result = propertyRepository.findByNameContainingIgnoreCase("Casa Bonita");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Casa Bonita");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldMatchCaseInsensitively() {
        PropertyEntity property = new PropertyEntity();
        property.setName("Casa Bonita");
        property.setRooms(3);
        property.setPrice(new BigDecimal("250000.00"));
        property.setType(Type.HOUSE);
        entityManager.persistAndFlush(property);

        Optional<PropertyEntity> result = propertyRepository.findByNameContainingIgnoreCase("CASA BONITA");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Casa Bonita");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldMatchPartialName() {
        PropertyEntity property = new PropertyEntity();
        property.setName("Casa Bonita no Centro");
        property.setRooms(2);
        property.setPrice(new BigDecimal("180000.00"));
        property.setType(Type.HOUSE);
        entityManager.persistAndFlush(property);

        Optional<PropertyEntity> result = propertyRepository.findByNameContainingIgnoreCase("Bonita");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Casa Bonita no Centro");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnEmpty_whenNoMatch() {
        PropertyEntity property = new PropertyEntity();
        property.setName("Casa Bonita");
        property.setRooms(3);
        property.setPrice(new BigDecimal("250000.00"));
        property.setType(Type.HOUSE);
        entityManager.persistAndFlush(property);

        Optional<PropertyEntity> result = propertyRepository.findByNameContainingIgnoreCase("Apartamento");

        assertThat(result).isEmpty();
    }
}
