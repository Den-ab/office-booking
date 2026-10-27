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
import ru.otus.pw.controllers.request_dtos.AreaDTO;
import ru.otus.pw.controllers.response_dtos.AreaResponseDTO;
import ru.otus.pw.services.AreaService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с пространствами.
 */
@RequiredArgsConstructor
@RestController
public class AreaController {
    //region Fields

    /**
     * Сервис для работы с пространствами.
     */
    private final AreaService areaService;

    //endregion
    //region Public

    /**
     * Возвращает список пространств.
     *
     * @return Список пространств.
     */
    @GetMapping(value = "/areas")
    public ResponseEntity<List<AreaResponseDTO>> findAllAreas() {

        final List<AreaResponseDTO> areas = this.areaService.findAll();

        return ResponseEntity.ok(areas);
    }

    /**
     * Создает пространство.
     *
     * @param area Пространство.
     *
     * @return Созданное пространство.
     */
    @PostMapping(value = "/areas")
    public ResponseEntity<AreaResponseDTO> createArea(@RequestBody @Valid AreaDTO area) {

        final AreaResponseDTO savedArea = this.areaService.create(area);

        return ResponseEntity.ok(savedArea);
    }

    /**
     * Обновляет пространство.
     *
     * @param area Пространство.
     *
     * @return Обновленное пространство.
     */
    @PutMapping(value = "/areas")
    public ResponseEntity<AreaResponseDTO> updateArea(@RequestBody @Valid AreaDTO area) {

        final AreaResponseDTO savedArea = this.areaService.update(area);

        return ResponseEntity.ok(savedArea);
    }

    /**
     * Удаляет пространство.
     *
     * @param id Идентификатор пространства.
     */
    @DeleteMapping(value = "/areas/{id}")
    public void deleteArea(@PathVariable String id) {

        this.areaService.delete(UUID.fromString(id));
    }

    //endregion
}
