package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.controller;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos.PropertyDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyEntity create(@RequestBody @Valid PropertyDto dto) {
        return propertyService.create(dto);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Page<PropertyEntity> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return propertyService.findAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public PropertyEntity findByName(@RequestParam String name) {
        return propertyService.findByName(name);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PropertyEntity update(@PathVariable String id, @Valid @RequestBody PropertyDto dto) {
        return propertyService.update(id, dto);
    }

    @PatchMapping("/{id}/toggle-active")
    @ResponseStatus(HttpStatus.OK)
    public PropertyEntity toggleActive(@PathVariable String id) {
        return propertyService.toggleActive(id);
    }

}
