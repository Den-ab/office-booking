package ru.otus.pw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.pw.models.AppUser;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий пользователей.
 */
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    //region Public

    /**
     * Возвращает пользователя по email.
     *
     * @param email Почта.
     *
     * @return Пользователь.
     */
    Optional<AppUser> findByEmail(String email);

    //endregion
}
