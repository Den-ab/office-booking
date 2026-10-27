package ru.otus.pw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.pw.controllers.request_dtos.BookDeskDTO;
import ru.otus.pw.controllers.response_dtos.BookingResponseDTO;
import ru.otus.pw.services.BookingService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с бронированиями.
 */
@RequiredArgsConstructor
@RestController
public class BookingController {
    //region Fields

    /**
     * Сервис для работы с бронированиями.
     */
    private final BookingService bookingService;

    //endregion
    //region

    /**
     * Возвращает список бронирований.
     *
     * @return Список бронирований.
     */
    @GetMapping(value = "/bookings")
    public ResponseEntity<List<BookingResponseDTO>> findAllBookings() {

        final List<BookingResponseDTO> bookings = this.bookingService.findAll();

        return ResponseEntity.ok(bookings);
    }

    /**
     * Создает бронирование.
     *
     * @param bookDeskDTO Данные бронирования.
     *
     * @return Созданное бронирование.
     */
    @PostMapping(value = "/bookings")
    public ResponseEntity<BookingResponseDTO> bookDesk(@RequestBody @Valid BookDeskDTO bookDeskDTO) {

        final UUID userId = UUID.fromString(bookDeskDTO.userId());
        final UUID deskId = UUID.fromString(bookDeskDTO.deskId());

        final BookingResponseDTO booking = this.bookingService.bookDesk(userId, deskId);

        return ResponseEntity.ok(booking);
    }

    /**
     * Удаляет бронирование.
     *
     * @param id Идентификатор бронирования.
     */
    @DeleteMapping(value = "/bookings/{id}")
    public void deleteBooking(@PathVariable String id) {

        final UUID bookingId = UUID.fromString(id);
        this.bookingService.delete(bookingId);
    }

    //endregion
}
