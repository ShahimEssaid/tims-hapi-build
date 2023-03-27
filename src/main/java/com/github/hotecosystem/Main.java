package com.github.hotecosystem;

import ca.uhn.fhir.jpa.starter.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(Application.class)
public class Main {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Main.class);
		application.run(args);
	}
}