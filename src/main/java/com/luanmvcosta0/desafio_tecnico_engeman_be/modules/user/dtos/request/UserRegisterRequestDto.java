package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestDto(
        @NotBlank(message = "O nome de usuário é obrigatório")
        String username,

        @Email(message = "O Email inválido")
        @NotBlank(message = "O Email é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String password,

        UserRole role
) {
}
