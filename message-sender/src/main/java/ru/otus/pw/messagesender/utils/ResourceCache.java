package ru.otus.pw.messagesender.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс для кэширования ресурсов, загружаемых из classpath.
 */
@Component
public class ResourceCache {
    //region Fields

    /**
     * Кэш ресурсов.
     */
    private final Map<String, Resource> cache = new ConcurrentHashMap<>();

    //endregion
    //region Public

    /**
     * Получает закэшированный ресурс. Если ресурс еще не загружен, загружает его и сохраняет в кэше.
     *
     * @param path Путь к ресурсу в classpath.
     * @return Ресурс из classpath.
     */
    public Resource getResource(String path) {

        return cache.computeIfAbsent(path, ClassPathResource::new);
    }

    /**
     * Очистка кэша.
     */
    public void clearCache() {

        cache.clear();
    }

    //endregion
}

