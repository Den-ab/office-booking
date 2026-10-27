package ru.otus.pw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.pw.models.Area;

import java.util.UUID;

/**
 * Репозиторий пространств.
 */
public interface AreaRepository extends JpaRepository<Area, UUID> { }
