package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos.PropertyDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyEntity create(PropertyDto dto) {
        PropertyEntity property = new PropertyEntity();
        property.setName(dto.getName());
        property.setRooms(dto.getRooms());
        property.setPrice(dto.getPrice());
        property.setType(dto.getType());

        return propertyRepository.save(property);
    }

    public Page<PropertyEntity> findAll(Pageable pageable) {
        return propertyRepository.findAll(pageable);
    }

    public PropertyEntity findByName(String name) {
        return propertyRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public PropertyEntity update(String id, PropertyDto dto) {
        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propriedade não encontrada pelo id: " + id));

        property.setName(dto.getName());
        property.setRooms(dto.getRooms());
        property.setPrice(dto.getPrice());
        property.setType(dto.getType());

        return propertyRepository.save(property);
    }

    public PropertyEntity toggleActive(String id) {
        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propriedade não encontrada pelo id: " + id));

        property.setActive(!property.getActive());

        return propertyRepository.save(property);
    }

}
