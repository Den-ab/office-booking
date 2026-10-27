package ru.otus.pw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.pw.controllers.response_dtos.BookingDeskResponseDTO;
import ru.otus.pw.controllers.response_dtos.BookingResponseDTO;
import ru.otus.pw.controllers.response_dtos.BookingUserResponseDTO;
import ru.otus.pw.controllers.response_dtos.DeskAreaResponseDTO;
import ru.otus.pw.models.AppUser;
import ru.otus.pw.models.Booking;
import ru.otus.pw.models.Desk;
import ru.otus.pw.repositories.AppUserRepository;
import ru.otus.pw.repositories.BookingRepository;
import ru.otus.pw.repositories.DeskRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с бронированиями.
 */
@RequiredArgsConstructor
@Service
public class BookingService {
    //region Fields

    /**
     * Репозиторий бронирований.
     */
    private final BookingRepository bookingRepository;

    /**
     * Репозиторий пользователей.
     */
    private final AppUserRepository userRepository;

    /**
     * Репозиторий столов.
     */
    private final DeskRepository deskRepository;

    /**
     * Сервис для работы с почтой.
     */
    private final MailSenderService mailSenderService;

    //endregion
    //region Public

    /**
     * Возвращает список бронирований.
     *
     * @return Список бронирований.
     */
    public List<BookingResponseDTO> findAll() {

        return this.bookingRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    /**
     * Создает бронирование.
     *
     * @param userId Идентификатор пользователя.
     * @param deskId Идентификатор стола.
     *
     * @return Созданное бронирование.
     */
    public BookingResponseDTO bookDesk(UUID userId, UUID deskId) {
        final Instant now = Instant.now();
        final ZoneId zoneId = ZoneId.systemDefault();
        final LocalDate today = now.atZone(zoneId).toLocalDate();
        final List<Booking> bookings = this.bookingRepository.findByUserIdAndDeskId(userId, deskId);
        if (bookings.stream().anyMatch(booking -> booking.getDate().atZone(zoneId).toLocalDate().equals(today))) {

            throw new IllegalStateException("You have already booked this desk for today.");
        }
        final Optional<AppUser> user = this.userRepository.findById(userId);
        final Optional<Desk> desk = this.deskRepository.findById(deskId);
        if (user.isEmpty() || desk.isEmpty()) {

            throw new IllegalStateException("User and/or desk don't exist.");
        }
        final Booking booking = Booking.builder().desk(desk.get()).user(user.get()).date(now).build();
        final Booking savedBook = this.bookingRepository.save(booking);
        this.mailSenderService.sendBookingMessageToMail(
            user.get().getEmail(),
            desk.get().getArea().getName(),
            desk.get().getNumber(),
            today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
        return this.toResponseDto(savedBook);
    }

    /**
     * Удаляет бронирование.
     *
     * @param bookingId Идентификатор бронирования.
     */
    public void delete(UUID bookingId) {

        this.bookingRepository.deleteById(bookingId);
    }

    //endregion
    //region Private

    /**
     * Преобразует сущность бронирования в DTO.
     *
     * @param booking Бронирование.
     *
     * @return DTO бронирования.
     */
    private BookingResponseDTO toResponseDto(Booking booking) {
        return new BookingResponseDTO(
            booking.getId() != null ? booking.getId().toString() : null,
            booking.getDate() != null ? booking.getDate().toString() : null,
            booking.getUser() == null
                ? null
                : new BookingUserResponseDTO(
                booking.getUser().getId() != null ? booking.getUser().getId().toString() : null
            ),
            booking.getDesk() == null
                ? null
                : new BookingDeskResponseDTO(
                booking.getDesk().getId() != null ? booking.getDesk().getId().toString() : null,
                booking.getDesk().getNumber(),
                booking.getDesk().getArea() == null
                    ? null
                    : new DeskAreaResponseDTO(
                    booking.getDesk().getArea().getId() != null
                        ? booking.getDesk().getArea().getId().toString()
                        : null,
                    booking.getDesk().getArea().getName()
                )
            )
        );
    }

    //endregion
}
