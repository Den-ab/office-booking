package ru.otus.pw.messagesender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>Точка старта приложения</p>
 */
@SpringBootApplication
public class Application {
	//region Public

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

	//endregion
}
