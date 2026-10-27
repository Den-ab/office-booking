package ru.otus.pw.messagesender.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Шаблоны уведомлений.
 */
@AllArgsConstructor
@Getter
public enum MessageTemplateType {
    //region Enum values

    /**
     * Базовый шаблон уведомления на электронную почту.
     */
    EMAIL_BASE_TEMPLATE(false),

    /**
     * Шаблон бронирования стола.
     */
    OFFICE_BOOKING_DESK_TEMPLATE(false),
    ;

    //endregion
    //region Fields

    /**
     * <p>Есть ли у темплейта базовый темплейт.</p>
     */
    private final boolean needBaseTemplate;

    //endregion

    //region Public

    /**
     * Возвращает наименование файла темплейта.
     *
     * @param extension Расширение
     */
    public String getTemplateFileName(String extension) {

        return Arrays.stream(this.name().split("_"))
            .map(word -> StringUtils.capitalize(word.toLowerCase(Locale.ROOT)))
            .collect(Collectors.joining("", "", extension));
    }

    //endregion
}
