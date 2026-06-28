package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.controller;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos.PropertyDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Propriedade", description = "- Gerenciador de propriedades (imóveis)")
public class PropertyController {

    private final PropertyService propertyService;

    @Operation(summary = "Cadastrar propriedade", description = "Cria um novo imóvel no sistema. Informe nome, quartos, preço e o tipo: CONDOMINIUM, HOUSE ou BUILDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imóvel cadastrado"),
            @ApiResponse(responseCode = "400", description = "Algum campo obrigatório está faltando ou com valor inválido")
    })
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyEntity create(@RequestBody @Valid PropertyDto dto) {
        return propertyService.create(dto);
    }

    @Operation(summary = "Listar propriedades", description = "Retorna os imóveis cadastrados com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
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

    @Operation(summary = "Buscar por nome", description = "Busca um imóvel pelo nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imóvel encontrado"),
            @ApiResponse(responseCode = "404", description = "Nenhum imóvel com esse nome foi encontrado")
    })
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public PropertyEntity findByName(@RequestParam String name) {
        return propertyService.findByName(name);
    }

    @Operation(summary = "Atualizar propriedade", description = "Atualiza os dados de um imóvel pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imóvel atualizado"),
            @ApiResponse(responseCode = "400", description = "Algum campo obrigatório está faltando ou com valor inválido"),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado para o ID informado")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PropertyEntity update(@PathVariable String id, @Valid @RequestBody PropertyDto dto) {
        return propertyService.update(id, dto);
    }

    @Operation(summary = "Ativar/desativar propriedade", description = "Liga ou desliga o imóvel. Chame de novo para reverter, funciona como um toggle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado para o ID informado")
    })
    @PatchMapping("/{id}/toggle-active")
    @ResponseStatus(HttpStatus.OK)
    public PropertyEntity toggleActive(@PathVariable String id) {
        return propertyService.toggleActive(id);
    }

}
