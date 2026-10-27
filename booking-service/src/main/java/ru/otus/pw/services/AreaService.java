package ru.otus.pw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.pw.controllers.request_dtos.AreaDTO;
import ru.otus.pw.controllers.response_dtos.AreaResponseDTO;
import ru.otus.pw.models.Area;
import ru.otus.pw.repositories.AreaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с пространствами.
 */
@RequiredArgsConstructor
@Service
public class AreaService {
    //region Fields

    /**
     * Репозиторий пространств.
     */
    private final AreaRepository areaRepository;

    //endregion
    //region Public

    /**
     * Возвращает список пространств.
     *
     * @return Список пространств.
     */
    public List<AreaResponseDTO> findAll() {

        return this.areaRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    /**
     * Создает пространство.
     *
     * @param area Пространство.
     *
     * @return Созданное пространство.
     */
    public AreaResponseDTO create(AreaDTO area) {

        return this.save(null, area.name());
    }

    /**
     * Обновляет пространство.
     *
     * @param area Пространство.
     *
     * @return Обновленное пространство.
     */
    public AreaResponseDTO update(AreaDTO area) {

        final UUID areaId = UUID.fromString(area.id());

        return this.save(areaId, area.name());
    }

    /**
     * Удаляет пространство.
     *
     * @param areaId Идентификатор пространства.
     */
    public void delete(UUID areaId) {

        this.areaRepository.deleteById(areaId);
    }

    //endregion
    //region Private

    /**
     * Сохраняет пространство.
     * Если идентификатор нулловый, то значит будет создание пространства. Иначе - обновление.
     *
     * @param areaId Идентификатор пространства.
     * @param name Наименование.
     *
     * @return Сохраненное пространство.
     */
    private AreaResponseDTO save(UUID areaId, String name) {

        final Area saved = this.areaRepository.save(Area.builder().id(areaId).name(name).build());
        return this.toResponseDto(saved);
    }

    /**
     * Преобразует сущность пространства в DTO.
     *
     * @param area Пространство.
     *
     * @return DTO пространства.
     */
    private AreaResponseDTO toResponseDto(Area area) {

        return new AreaResponseDTO(
            area.getId() != null ? area.getId().toString() : null,
            area.getName()
        );
    }

    //endregion
}
