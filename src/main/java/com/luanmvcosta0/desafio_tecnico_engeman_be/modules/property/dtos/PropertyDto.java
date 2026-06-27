package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.dtos;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.enums.Type;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDto {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer rooms;

    @NotNull
    @DecimalMin(value = "0.01")
    @Positive
    private BigDecimal price;

    @NotNull
    private Type type;

}
