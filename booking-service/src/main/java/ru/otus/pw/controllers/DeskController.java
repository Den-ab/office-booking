package ru.otus.pw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.pw.controllers.request_dtos.DeskDTO;
import ru.otus.pw.controllers.response_dtos.DeskResponseDTO;
import ru.otus.pw.models.Desk;
import ru.otus.pw.services.DeskService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы со столами.
 */
@RequiredArgsConstructor
@RestController
public class DeskController {
    //region Fields

    /**
     * Сервис для работы со столами.
     */
    private final DeskService deskService;

    //endregion
    //region Public

    /**
     * Возвращает список столов.
     *
     * @return Список столов.
     */
    @GetMapping(value = "/desks")
    public ResponseEntity<List<DeskResponseDTO>> findAllDesks() {

        final List<DeskResponseDTO> desks = this.deskService.findAll();

        return ResponseEntity.ok(desks);
    }

    /**
     * Создает стол.
     *
     * @param desk Данные стола.
     *
     * @return Созданный стол.
     */
    @PostMapping(value = "/desks")
    public ResponseEntity<DeskResponseDTO> createDesk(@RequestBody @Valid DeskDTO desk) {

        final DeskResponseDTO savedDesk = this.deskService.create(desk);

        return ResponseEntity.ok(savedDesk);
    }

    /**
     * Обновляет стол.
     *
     * @param desk Данные стола.
     *
     * @return Обновляет стол.
     */
    @PutMapping(value = "/desks")
    public ResponseEntity<DeskResponseDTO> updateDesk(@RequestBody @Valid DeskDTO desk) {

        final DeskResponseDTO savedDesk = this.deskService.update(desk);

        return ResponseEntity.ok(savedDesk);
    }

    /**
     * Удаляет стол.
     *
     * @param id Идентификатор стола.
     */
    @DeleteMapping(value = "/desks/{id}")
    public void deleteDesk(@PathVariable String id) {

        this.deskService.delete(UUID.fromString(id));
    }

    //endregion
}
