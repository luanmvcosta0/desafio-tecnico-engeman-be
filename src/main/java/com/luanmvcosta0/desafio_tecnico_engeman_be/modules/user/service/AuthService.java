package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.service;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserLoginRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.request.UserRegisterRequestDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserLoginResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.dtos.response.UserRegisterResponseDto;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.enums.UserRole;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.model.UserEntity;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserRegisterResponseDto register(UserRegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        UserEntity user = new UserEntity();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        boolean isFirstUser = userRepository.count() == 0;
        if (isFirstUser) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(dto.role() == UserRole.BROKER ? UserRole.BROKER : UserRole.CUSTOMER);
        }

        userRepository.save(user);

        return new UserRegisterResponseDto(user.getUsername(), user.getEmail());
    }

    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return new UserLoginResponseDto(token);
    }
}