package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos.PropertyDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyEntity create(PropertyDto dto) {
        PropertyEntity entity = new PropertyEntity();
        entity.setName(dto.getName());
        entity.setRooms(dto.getRooms());
        entity.setPrice(dto.getPrice());
        entity.setType(dto.getType());

        return propertyRepository.save(entity);
    }

    public List<PropertyEntity> findAll() {
        return propertyRepository.findAll();
    }

    public PropertyEntity findByName(String name) {
        return propertyRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
