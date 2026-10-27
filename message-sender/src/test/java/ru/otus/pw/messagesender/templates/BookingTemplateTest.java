package ru.otus.pw.messagesender.templates;

import freemarker.template.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.otus.pw.messagesender.config.email.SenderConfig;
import ru.otus.pw.messagesender.models.MessageTemplateType;
import ru.otus.pw.messagesender.services.email.EmailTemplateService;
import ru.otus.pw.messagesender.services.i18n.I18nService;
import ru.otus.pw.messagesender.services.i18n.I18nServiceImpl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingTemplateTest {

    private static final String TEST_RESULT_BOOKING_TEMPLATE = "<!DOCTYPE HTML>\n" +
        "<html>\n" +
        "<head>\n" +
        "  <style>\n" +
        "      body {\n" +
        "          padding: 0;\n" +
        "          margin: 0;\n" +
        "          background-color: #f8f8f8;\n" +
        "          color: #2d2d2d;\n" +
        "          font-size: 12px;\n" +
        "          line-height: 24px;\n" +
        "          font-family: Verdana, sans-serif;\n" +
        "      }\n" +
        "\n" +
        "      .container {\n" +
        "          width: 610px;\n" +
        "          margin: auto;\n" +
        "          padding-top: 40px;\n" +
        "          padding-bottom: 40px;\n" +
        "          box-sizing: border-box;\n" +
        "      }\n" +
        "\n" +
        "      .logo {\n" +
        "          height: 32px;\n" +
        "      }\n" +
        "\n" +
        "      .main {\n" +
        "          width: 610px;\n" +
        "          margin: auto;\n" +
        "          border: 1px solid #f0f0f0;\n" +
        "          border-bottom: 1px solid #FFFFFF;\n" +
        "          background-color: #FFFFFF;\n" +
        "      }\n" +
        "\n" +
        "      .content {\n" +
        "          margin: 40px;\n" +
        "          margin-bottom: 32px;\n" +
        "      }\n" +
        "\n" +
        "      .footer {\n" +
        "          width: 610px;\n" +
        "          margin: auto;\n" +
        "          border: 1px solid #f0f0f0;\n" +
        "          background-color: #FFFFFF;\n" +
        "      }\n" +
        "\n" +
        "      .footer-content {\n" +
        "          margin: 40px;\n" +
        "          margin-top: 32px;\n" +
        "      }\n" +
        "\n" +
        "      .signature {\n" +
        "          width: 50%;\n" +
        "          float: left;\n" +
        "          text-align: left;\n" +
        "      }\n" +
        "\n" +
        "      .contact {\n" +
        "          width: 50%;\n" +
        "          float: right;\n" +
        "          text-align: right;\n" +
        "      }\n" +
        "\n" +
        "      .link {\n" +
        "          color: #00af7d;\n" +
        "          text-decoration: none;\n" +
        "      }\n" +
        "\n" +
        "      .clear {\n" +
        "          clear: both;\n" +
        "      }\n" +
        "  </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "<div class=\"container\">\n" +
        "  <img class=\"logo\" src=\"cid:logo_cid\" alt=\"logo\">\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"main\">\n" +
        "  <div class=\"content\">\n" +
        "    <p>\n" +
        "        Вы забронировали стол 11 на 03.03.2026 число в пространстве Test papka.\n" +
        "        Пожалуйста, если вы передумали, сообщите об этом заранее.\n" +
        "    </p>\n" +
        "  </div>\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"footer\">\n" +
        "  <div class=\"footer-content\">\n" +
        "    <p>\n" +
        "        Возникли вопросы? Обращайтесь в поддержку через чат в приложении\n" +
        "      <a class=\"link\" href=\"mailto:support@example.com\">\n" +
        "          напишите нам\n" +
        "      </a>\n" +
        "        или на почту, указанную выше. Мы всегда рады помочь!\n" +
        "    </p>\n" +
        "  </div>\n" +
        "</div>\n" +
        "\n" +
        "<div class=\"container\">\n" +
        "  <div class=\"signature\">\n" +
        "    <br>\n" +
        "      С уважением,\n" +
        "    <br>\n" +
        "      Служба заботы о клиентах\n" +
        "    <br>\n" +
        "      Команда Office Booking\n" +
        "  </div>\n" +
        "\n" +
        "  <div class=\"contact\">\n" +
        "    <br>\n" +
        "    <a class=\"link\" href=\"https://example.com\">\n" +
        "        example.com\n" +
        "    </a>\n" +
        "    <br>\n" +
        "    <a class=\"link\" href=\"mailto:support@example.com\">\n" +
        "        support@example.com\n" +
        "    </a>\n" +
        "    <br>\n" +
        "    <a class=\"link\" href=\"tel:+1-555-0100\">\n" +
        "        +1-555-0100\n" +
        "    </a>\n" +
        "  </div>\n" +
        "\n" +
        "  <div class=\"clear\"></div>\n" +
        "</div>\n" +
        "</body>\n" +
        "</html>\n";

    @Mock
    private Configuration freMarkerConfiguration;

    @Mock
    private I18nService i18nService;

    @InjectMocks
    private EmailTemplateService emailTemplateService;

    private Map<String, Object> model;


    @BeforeEach
    void setUp() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");

        SenderConfig senderConfig = new SenderConfig() {
            @Override
            public String getLogoPath() {
                return null;
            }

            @Override
            public Locale getLocale() {
                return Locale.forLanguageTag("ru-RU");
            }
        };

        i18nService = new I18nServiceImpl(messageSource, senderConfig);

        freMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        freMarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/");

        emailTemplateService = new EmailTemplateService(freMarkerConfiguration);

        model = new HashMap<>();
        model.put("lang", "ru");
        model.put("content", "<p>Test Content</p>");
        model.put("tableNumber", "11");
        model.put("bookingDate", "03.03.2026");
        model.put("spaceName", "Test papka");
    }

    @Test
    void testGenerateEmailBaseTemplate() {
        String result = emailTemplateService.generateEmailTemplate(
            model,
            "",
            MessageTemplateType.OFFICE_BOOKING_DESK_TEMPLATE,
            i18nService
        );

        assertEquals(result, TEST_RESULT_BOOKING_TEMPLATE);
    }
}
