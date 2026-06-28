package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginUserRequest(@NotEmpty(message = "O email é obrigatório") String email,
                               @NotEmpty(message = "A senha é obrigatória") String password) {
}
