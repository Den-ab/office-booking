package ru.otus.pw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.pw.controllers.request_dtos.LoginDTO;
import ru.otus.pw.controllers.response_dtos.AuthResponse;
import ru.otus.pw.controllers.response_dtos.UserResponseDTO;
import ru.otus.pw.services.AppUserDetailsService;
import ru.otus.pw.services.JwtService;
import ru.otus.pw.services.UserService;

/**
 * Контроллер для работы с пользователями.
 */
@RequiredArgsConstructor
@RestController
public class UserController {
    //region Fields

    /**
     * Сервис для управления аутентификацией.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Сервис для работы с данными пользователя.
     */
    private final AppUserDetailsService appUserDetailsService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Сервис для работы с JWT.
     */
    private final JwtService jwtService;

    //endregion
    //region Public

    /**
     * Возвращает список пространств.
     *
     * @return Список пространств.
     */
    @PostMapping(value = "/auth/login")
    public AuthResponse login(@RequestBody @Valid LoginDTO loginDTO) {

        this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password())
        );

        final UserResponseDTO appUser = this.userService.findByEmail(loginDTO.email())
            .orElseThrow(() -> new IllegalStateException("Can't find user with email " + loginDTO.email()));

        UserDetails userDetails = this.appUserDetailsService.loadUserByUsername(loginDTO.email());
        String token = this.jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .map(authority -> authority.replace("ROLE_", ""))
            .orElse("USER");

        return new AuthResponse(appUser.id(), token, role);
    }

    //endregion
}
