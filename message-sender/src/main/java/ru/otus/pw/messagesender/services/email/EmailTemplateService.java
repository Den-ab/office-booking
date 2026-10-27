package ru.otus.pw.messagesender.services.email;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import freemarker.template.Configuration;
import ru.otus.pw.messagesender.exceptions.EmailSenderException;
import ru.otus.pw.messagesender.models.MessageTemplateType;
import ru.otus.pw.messagesender.services.i18n.I18nService;
import ru.otus.pw.messagesender.providers.MessageProvider;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Сервис сборки шаблонов для отправки уведомлений на электронную почту.
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "message-sender")
public class EmailTemplateService {
    //endregion Constants

    /**
     * Поле контент в шаблонах отправки уведомления на электронную почту.
     */
    private static final String CONTENT_FIELD = "content";

    /**
     * Поле языка в шаблонах отправки уведомления на электронную почту.
     */
    private static final String LANG_FIELD = "lang";

    /**
     * Поле языка по умолчанию в шаблонах отправки уведомления на электронную почту.
     */
    private static final String LANG_FIELD_DEFAULT_VALUE = "ru";

    /**
     * Поле интернационализации в шаблонах отправки уведомления на электронную почту.
     */
    private static final String I18N_SERVICE_FIELD = "i18nService";

    /**
     * Версия файлов шаблонов.
     */
    private static final String EXTENSION = ".ftl";

    //endregion
    //region Fields

    /**
     * Сервис сборки шаблонов.
     */
    private final Configuration freMarkerConfiguration;

    //endregion
    //region Public

    /**
     * Создает и возвращает текст уведомления для отправки на электронную почту, по указанному шаблону.
     *
     * TODO переделать Map<String, Object> model на dto.
     *
     * @param model Данные которые должны быть в шаблоне.
     * @param emailPath Расположение шаблонов в файловой системе.
     * @param templateType Тип шаблона.
     * @param i18nService Сервис интернационализации.
     */
    public String generateEmailTemplate(
        Map<String, Object> model,
        String emailPath,
        MessageTemplateType templateType,
        I18nService i18nService
    ) {
        if (templateType.isNeedBaseTemplate()) {

            String content = processTemplate(
                templateType.getTemplateFileName(EXTENSION),
                model,
                emailPath,
                i18nService
            );
            model.put(CONTENT_FIELD, content);
            templateType = MessageTemplateType.EMAIL_BASE_TEMPLATE;
        }

        return processTemplate(
            templateType.getTemplateFileName(EXTENSION),
            model,
            emailPath,
            i18nService
        );
    }

    //endregion
    //region Private

    /**
     * Собирает шаблон для отправки уведомлений.
     *
     * @param templateName Наименование шаблона.
     * @param model Данные которые должны быть в шаблоне.
     * @param emailPath Путь до шаблона в файловой системе.
     * @param i18nService Сервис интернационализации.
     */
    private String processTemplate(
        String templateName,
        Map<String, Object> model,
        String emailPath,
        I18nService i18nService
    ) {
        try {
            String lang = (String) model.getOrDefault(LANG_FIELD, LANG_FIELD_DEFAULT_VALUE);
            model.put(I18N_SERVICE_FIELD, (MessageProvider) (key, args) -> i18nService.getMessage(lang, key, args));

            var template = freMarkerConfiguration.getTemplate(emailPath + templateName);
            var writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {

            log.error("Failed to generate email template: {}", templateName, e);
            throw new EmailSenderException("Failed to generate email template: " + templateName, e);
        }
    }

    //endregion
}
