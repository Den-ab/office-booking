package ru.otus.pw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с пользователями в SS.
 */
@RequiredArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {
    //region Fields

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    //endregion
    //region Public

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return this.userService.findEntityByEmail(email)
            .map(user -> User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build()
            )
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    //endregion
}
