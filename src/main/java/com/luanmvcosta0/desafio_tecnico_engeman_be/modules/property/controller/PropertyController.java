package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.controller;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

}
