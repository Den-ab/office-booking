package ru.otus.pw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.pw.controllers.response_dtos.BookingResponseDTO;
import ru.otus.pw.models.AppUser;
import ru.otus.pw.models.Area;
import ru.otus.pw.models.Booking;
import ru.otus.pw.models.Desk;
import ru.otus.pw.repositories.AppUserRepository;
import ru.otus.pw.repositories.BookingRepository;
import ru.otus.pw.repositories.DeskRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private DeskRepository deskRepository;

    @Mock
    private MailSenderService mailSenderService;

    @InjectMocks
    private BookingService bookingService;

    private final UUID userId = UUID.randomUUID();

    private final UUID deskId = UUID.randomUUID();

    private final AppUser user = AppUser.builder().id(userId).email("user@example.com").build();

    private final Area area = Area.builder().id(UUID.randomUUID()).name("Open space").build();

    private final Desk desk = Desk.builder().id(deskId).number(7).area(area).build();

    @Test
    @DisplayName("Booking a free desk saves the booking and sends a confirmation email")
    void bookDeskSavesBookingAndSendsEmail() {
        stubUserAndDesk();
        when(bookingRepository.findByUserIdAndDeskId(userId, deskId)).thenReturn(List.of());

        BookingResponseDTO response = bookingService.bookDesk(userId, deskId);

        assertThat(response.id()).isNotNull();
        assertThat(response.user().id()).isEqualTo(userId.toString());
        assertThat(response.desk().number()).isEqualTo(7);
        assertThat(response.desk().area().name()).isEqualTo("Open space");
        verify(mailSenderService).sendBookingMessageToMail(
            eq("user@example.com"), eq("Open space"), eq(7), anyString()
        );
    }

    @Test
    @DisplayName("The same user cannot book the same desk twice on one day")
    void bookDeskRejectsSecondBookingOnSameDay() {
        Booking existing = Booking.builder().id(UUID.randomUUID()).user(user).desk(desk).date(Instant.now()).build();
        when(bookingRepository.findByUserIdAndDeskId(userId, deskId)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> bookingService.bookDesk(userId, deskId))
            .isInstanceOf(IllegalStateException.class);

        verify(bookingRepository, never()).save(any());
        verify(mailSenderService, never()).sendBookingMessageToMail(anyString(), anyString(), anyInt(), anyString());
    }

    @Test
    @DisplayName("A booking from a previous day does not block a new one")
    void bookDeskAllowsBookingWhenPreviousOneWasYesterday() {
        Booking yesterday = Booking.builder()
            .id(UUID.randomUUID())
            .user(user)
            .desk(desk)
            .date(Instant.now().minus(1, ChronoUnit.DAYS))
            .build();
        stubUserAndDesk();
        when(bookingRepository.findByUserIdAndDeskId(userId, deskId)).thenReturn(List.of(yesterday));

        BookingResponseDTO response = bookingService.bookDesk(userId, deskId);

        assertThat(response.id()).isNotNull();
    }

    @Test
    @DisplayName("Booking for an unknown user fails without saving anything")
    void bookDeskFailsForUnknownUser() {
        when(bookingRepository.findByUserIdAndDeskId(userId, deskId)).thenReturn(List.of());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(deskRepository.findById(deskId)).thenReturn(Optional.of(desk));

        assertThatThrownBy(() -> bookingService.bookDesk(userId, deskId))
            .isInstanceOf(IllegalStateException.class);

        verify(bookingRepository, never()).save(any());
    }

    private void stubUserAndDesk() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(deskRepository.findById(deskId)).thenReturn(Optional.of(desk));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            return booking.toBuilder().id(UUID.randomUUID()).build();
        });
    }
}
