package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

}
