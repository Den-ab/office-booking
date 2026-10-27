package ru.otus.pw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.pw.models.Booking;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий бронирований.
 */
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Возвращает все бронирования.
     *
     * @return Бронирования.
     */
    @EntityGraph(attributePaths = {"user", "desk", "desk.area"})
    List<Booking> findAll();

    List<Booking> findByUserIdAndDeskId(UUID userId, UUID deskId);
}
