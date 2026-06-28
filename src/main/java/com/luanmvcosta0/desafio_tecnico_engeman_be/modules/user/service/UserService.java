package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

}
