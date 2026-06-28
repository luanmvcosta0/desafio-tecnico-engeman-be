package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.auth.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @Email(message = "Email inválido")
        @NotBlank(message = "O Email é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
