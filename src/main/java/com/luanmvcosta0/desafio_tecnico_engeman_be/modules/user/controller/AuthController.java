package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.controller;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserLoginRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserRegisterRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserLoginResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserRegisterResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Criar conta", description = "Cria um novo usuário. O papel (role) pode ser BROKER ou CUSTOMER, se for o primeiro cadastro do sistema, o usuário vira ADMIN automaticamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada, retorna username e email"),
            @ApiResponse(responseCode = "400", description = "Algum campo inválido ou faltando"),
            @ApiResponse(responseCode = "409", description = "Esse email já está em uso")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponseDto register(@RequestBody @Valid UserRegisterRequestDto dto) {
        return authService.register(dto);
    }

    @Operation(summary = "Entrar", description = "Autentica com email e senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login ok, retorna o token JWT"),
            @ApiResponse(responseCode = "400", description = "Email ou senha inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais incorretas")
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto dto) {
        return authService.login(dto);
    }
}