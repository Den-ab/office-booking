package ru.otus.pw.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.pw.controllers.request_dtos.DeskDTO;
import ru.otus.pw.controllers.response_dtos.DeskResponseDTO;
import ru.otus.pw.models.Area;
import ru.otus.pw.models.Desk;
import ru.otus.pw.repositories.AreaRepository;
import ru.otus.pw.repositories.DeskRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeskServiceTest {

    @Mock
    private DeskRepository deskRepository;

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private DeskService deskService;

    @Test
    @DisplayName("A desk is created inside an existing area")
    void createDeskInExistingArea() {
        UUID areaId = UUID.randomUUID();
        Area area = Area.builder().id(areaId).name("Open space").build();
        when(areaRepository.findById(areaId)).thenReturn(Optional.of(area));
        when(deskRepository.save(any(Desk.class))).thenAnswer(invocation -> {
            Desk desk = invocation.getArgument(0);
            return desk.toBuilder().id(UUID.randomUUID()).build();
        });

        DeskResponseDTO response = deskService.create(new DeskDTO(null, areaId.toString(), 3));

        assertThat(response.id()).isNotNull();
        assertThat(response.number()).isEqualTo(3);
        assertThat(response.area().id()).isEqualTo(areaId.toString());
        assertThat(response.area().name()).isEqualTo("Open space");
    }

    @Test
    @DisplayName("Creating a desk in an unknown area fails")
    void createDeskInUnknownAreaFails() {
        UUID areaId = UUID.randomUUID();
        when(areaRepository.findById(areaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deskService.create(new DeskDTO(null, areaId.toString(), 3)))
            .isInstanceOf(EntityNotFoundException.class);

        verify(deskRepository, never()).save(any());
    }
}
