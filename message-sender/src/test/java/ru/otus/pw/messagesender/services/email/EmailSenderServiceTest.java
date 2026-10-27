package ru.otus.pw.messagesender.services.email;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.core.io.Resource;
import ru.otus.pw.messagesender.config.email.EmailConfig;
import ru.otus.pw.messagesender.exceptions.EmailSenderException;
import ru.otus.pw.messagesender.services.email.EmailSenderService;
import ru.otus.pw.messagesender.utils.ResourceCache;

import java.io.ByteArrayInputStream;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailConfig emailConfig;

    @Mock
    private ResourceCache resourceCache;

    @Mock
    private Resource resource;

    @InjectMocks
    private EmailSenderService emailSenderService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() throws Exception {

        mimeMessage = new MimeMessage((Session) null);
        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        Mockito.when(emailConfig.getEmailSenderAddress()).thenReturn("sender@example.com");
        Mockito.when(emailConfig.getLogoPath()).thenReturn("images/logo.png");

        lenient().when(resourceCache.getResource("images/logo.png")).thenReturn(resource);
        lenient().when(resource.getFilename()).thenReturn("logo.png");
        lenient().when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
    }

    @Test
    void testSendMessage_success() {

        String toAddress = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        emailSenderService.sendMessage(toAddress, subject, text, null);

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void testSendMessage_failure() {

        String toAddress = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        doThrow(new RuntimeException("Sending failed")).when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(EmailSenderException.class, () -> emailSenderService.sendMessage(toAddress, subject, text, null));

        verify(javaMailSender).send(any(MimeMessage.class));
    }
}
