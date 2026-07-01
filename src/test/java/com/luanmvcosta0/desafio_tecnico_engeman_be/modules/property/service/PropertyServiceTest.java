package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos.PropertyDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.enums.Type;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;

    private PropertyDto dto;
    private PropertyEntity entity;

    @BeforeEach
    void setUp() {
        dto = new PropertyDto("Casa Teste", 3, new BigDecimal("250000.00"), Type.HOUSE);
        entity = new PropertyEntity("id-1", "Casa Teste", 3, new BigDecimal("250000.00"), Type.HOUSE, true);
    }

    @Test
    void create_shouldSaveAndReturnProperty() {
        when(propertyRepository.save(any(PropertyEntity.class))).thenReturn(entity);

        PropertyEntity result = propertyService.create(dto);

        assertThat(result.getName()).isEqualTo("Casa Teste");
        assertThat(result.getRooms()).isEqualTo(3);
        assertThat(result.getPrice()).isEqualByComparingTo("250000.00");
        assertThat(result.getType()).isEqualTo(Type.HOUSE);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    void create_shouldMapDtoFieldsToEntity() {
        ArgumentCaptor<PropertyEntity> captor = ArgumentCaptor.forClass(PropertyEntity.class);
        when(propertyRepository.save(captor.capture())).thenReturn(entity);

        propertyService.create(dto);

        PropertyEntity saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(dto.getName());
        assertThat(saved.getRooms()).isEqualTo(dto.getRooms());
        assertThat(saved.getPrice()).isEqualByComparingTo(dto.getPrice());
        assertThat(saved.getType()).isEqualTo(dto.getType());
    }

    @Test
    void findAll_shouldReturnPageOfProperties() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<PropertyEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(propertyRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<PropertyEntity> result = propertyService.findAll(null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Casa Teste");
    }

    @Test
    void findByName_shouldReturnProperty_whenExists() {
        when(propertyRepository.findByNameContainingIgnoreCase("casa")).thenReturn(Optional.of(entity));

        PropertyEntity result = propertyService.findByName("casa");

        assertThat(result.getName()).isEqualTo("Casa Teste");
        assertThat(result.getId()).isEqualTo("id-1");
    }

    @Test
    void findByName_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(propertyRepository.findByNameContainingIgnoreCase("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.findByName("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void update_shouldReturnUpdatedProperty_whenPropertyExists() {
        PropertyDto updatedDto = new PropertyDto("Casa Atualizada", 4, new BigDecimal("300000.00"), Type.CONDOMINIUM);
        PropertyEntity updatedEntity = new PropertyEntity("id-1", "Casa Atualizada", 4, new BigDecimal("300000.00"), Type.CONDOMINIUM, true);
        when(propertyRepository.findById("id-1")).thenReturn(Optional.of(entity));
        when(propertyRepository.save(any(PropertyEntity.class))).thenReturn(updatedEntity);

        PropertyEntity result = propertyService.update("id-1", updatedDto);

        assertThat(result.getName()).isEqualTo("Casa Atualizada");
        assertThat(result.getRooms()).isEqualTo(4);
        assertThat(result.getPrice()).isEqualByComparingTo("300000.00");
        assertThat(result.getType()).isEqualTo(Type.CONDOMINIUM);
    }

    @Test
    void update_shouldApplyDtoValuesToExistingEntity() {
        PropertyDto updatedDto = new PropertyDto("Nome Novo", 2, new BigDecimal("100000.00"), Type.BUILDING);
        ArgumentCaptor<PropertyEntity> captor = ArgumentCaptor.forClass(PropertyEntity.class);
        when(propertyRepository.findById("id-1")).thenReturn(Optional.of(entity));
        when(propertyRepository.save(captor.capture())).thenReturn(entity);

        propertyService.update("id-1", updatedDto);

        PropertyEntity saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Nome Novo");
        assertThat(saved.getRooms()).isEqualTo(2);
        assertThat(saved.getType()).isEqualTo(Type.BUILDING);
    }

    @Test
    void update_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(propertyRepository.findById("nao-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.update("nao-existe", dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void toggleActive_shouldDeactivateProperty_whenCurrentlyActive() {
        when(propertyRepository.findById("id-1")).thenReturn(Optional.of(entity));
        when(propertyRepository.save(any(PropertyEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PropertyEntity result = propertyService.toggleActive("id-1");

        assertThat(result.getActive()).isFalse();
    }

    @Test
    void toggleActive_shouldActivateProperty_whenCurrentlyInactive() {
        PropertyEntity inactiveEntity = new PropertyEntity("id-2", "Casa Inativa", 2, new BigDecimal("150000.00"), Type.HOUSE, false);
        when(propertyRepository.findById("id-2")).thenReturn(Optional.of(inactiveEntity));
        when(propertyRepository.save(any(PropertyEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PropertyEntity result = propertyService.toggleActive("id-2");

        assertThat(result.getActive()).isTrue();
    }

    @Test
    void toggleActive_shouldCallSaveWithToggledEntity() {
        ArgumentCaptor<PropertyEntity> captor = ArgumentCaptor.forClass(PropertyEntity.class);
        when(propertyRepository.findById("id-1")).thenReturn(Optional.of(entity));
        when(propertyRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        propertyService.toggleActive("id-1");

        verify(propertyRepository).save(captor.getValue());
        assertThat(captor.getValue().getActive()).isFalse();
    }

    @Test
    void toggleActive_shouldThrowNotFound_whenPropertyDoesNotExist() {
        when(propertyRepository.findById("nao-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService.toggleActive("nao-existe"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void create_shouldSetActiveToTrue_byDefault() {
        ArgumentCaptor<PropertyEntity> captor = ArgumentCaptor.forClass(PropertyEntity.class);
        when(propertyRepository.save(captor.capture())).thenReturn(entity);

        propertyService.create(dto);

        assertThat(captor.getValue().getActive()).isTrue();
    }

    @Test
    void update_shouldPreserveActiveFlag_whenEntityIsInactive() {
        PropertyEntity inactiveEntity = new PropertyEntity("id-1", "Casa Teste", 3, new BigDecimal("250000.00"), Type.HOUSE, false);
        PropertyDto updatedDto = new PropertyDto("Casa Nova", 2, new BigDecimal("200000.00"), Type.BUILDING);
        ArgumentCaptor<PropertyEntity> captor = ArgumentCaptor.forClass(PropertyEntity.class);
        when(propertyRepository.findById("id-1")).thenReturn(Optional.of(inactiveEntity));
        when(propertyRepository.save(captor.capture())).thenReturn(inactiveEntity);

        propertyService.update("id-1", updatedDto);

        assertThat(captor.getValue().getActive()).isFalse();
    }
}
