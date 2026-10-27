package ru.otus.pw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.pw.controllers.response_dtos.UserResponseDTO;
import ru.otus.pw.models.AppUser;
import ru.otus.pw.repositories.AppUserRepository;

import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 */
@RequiredArgsConstructor
@Service
public class UserService {
    //region Fields

    /**
     * Репозиторий пользователей.
     */
    private final AppUserRepository userRepository;

    //endregion
    //region Public

    /**
     * Возвращает пользователя по логину.
     *
     * @param email Почта.
     *
     * @return Пользователь.
     */
    public Optional<UserResponseDTO> findByEmail(String email) {

        return this.userRepository.findByEmail(email).map(this::toResponseDto);
    }

    /**
     * Возвращает сущность пользователя по логину.
     *
     * @param email Почта.
     *
     * @return Сущность пользователя.
     */
    public Optional<AppUser> findEntityByEmail(String email) {

        return this.userRepository.findByEmail(email);
    }

    //endregion
    //region Private

    /**
     * Преобразует сущность пользователя в DTO.
     *
     * @param user Пользователь.
     *
     * @return DTO пользователя.
     */
    private UserResponseDTO toResponseDto(AppUser user) {

        return new UserResponseDTO(
            user.getId() != null ? user.getId().toString() : null,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole() != null ? user.getRole().name() : null
        );
    }

    //endregion
}
