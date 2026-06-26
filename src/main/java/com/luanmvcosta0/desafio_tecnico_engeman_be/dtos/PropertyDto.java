package com.luanmvcosta0.desafio_tecnico_engeman_be.dtos;

import com.luanmvcosta0.desafio_tecnico_engeman_be.enums.Type;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private BigDecimal price;

    @NotNull
    private Type type;

}
