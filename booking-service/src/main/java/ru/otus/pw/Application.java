package ru.otus.pw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.otus.pw.config.jwt.JwtProperties;
import ru.otus.pw.config.mail_config.MailConfig;
import ru.otus.pw.config.rabbit.MessagingProperties;

@SpringBootApplication
@EnableConfigurationProperties({ MailConfig.class, MessagingProperties.class, JwtProperties.class })
public class Application {
    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
