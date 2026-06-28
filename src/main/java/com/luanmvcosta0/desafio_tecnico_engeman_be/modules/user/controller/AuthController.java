package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.controller;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserLoginRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserRegisterRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserLoginResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserRegisterResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponseDto register(@RequestBody @Valid UserRegisterRequestDto dto) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto dto) {
        return authService.login(dto);
    }
}