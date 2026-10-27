package ru.otus.pw.messagesender.services.email;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.otus.pw.messagesender.exceptions.EmailSenderException;
import ru.otus.pw.messagesender.config.email.EmailConfig;
import ru.otus.pw.messagesender.utils.ResourceCache;

/**
 * Сервис отправки сообщения на электронную почту.
 */
@Component
@RequiredArgsConstructor
@Slf4j(topic = "message-sender")
public class EmailSenderService {
    //region Constants

    /**
     * Id логотипа в сообщении.
     */
    public static final String LOGO_CID = "logo_cid";

    /**
     * Наше имя отправителя.
     */
    public static final String SENDER_NAME = "Office Booking";

    /**
     * Тип кодировки сообщения.
     */
    private static final String ENCODING = "UTF-8";

    //endregion
    //region Fields

    /**
     * Сервис для формирования e-mail сообщения.
     */
    private final JavaMailSender javaMailSender;

    /**
     * Конфиг электронной почты.
     */
    private final EmailConfig emailConfig;

    /**
     * Класс для кэширования ресурсов, загружаемых из classpath.
     */
    private final ResourceCache resourceCache;

    //endregion
    //region Public

    /**
     * Формирует и отправляет сообщение на указанную электронную почту.
     *
     * @param to Кому отправить сообщение.
     * @param subject Тема сообщения.
     * @param text Текст сообщения.
     */
    public void sendMessage(String to, String subject, String text, String emailSender) {
        try {
            var mimeMessage = javaMailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, ENCODING);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(new InternetAddress(
                emailSender != null && !emailSender.isBlank() ? emailSender : emailConfig.getEmailSenderAddress(),
                SENDER_NAME)
            );
            helper.setSubject(subject);
            helper.setText(text, true);
            if (resourceCache.getResource(emailConfig.getLogoPath()).getFilename() == null) {
                throw new EmailSenderException(
                    "Resource filename is null for path: " + emailConfig.getLogoPath(),
                    new Exception()
                );
            }
            helper.addInline(LOGO_CID, resourceCache.getResource(emailConfig.getLogoPath()));
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Failed while send email template to email: " + to);
            throw new EmailSenderException("Failed while send email template to email: " + to, e);
        }
    }

    //endregion
}
