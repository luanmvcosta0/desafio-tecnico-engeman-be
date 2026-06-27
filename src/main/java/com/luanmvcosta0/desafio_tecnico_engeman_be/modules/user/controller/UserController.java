package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.controller;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

}
