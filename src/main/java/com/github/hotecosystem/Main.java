package com.github.hotecosystem;

import ca.uhn.fhir.jpa.starter.Application;
import com.essaid.fhir.hapi.ext.component.HapiExitManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(Application.class)
public class Main {

  public static void main(String[] args) throws InterruptedException {
    SpringApplication application = new SpringApplication(Main.class);
    ConfigurableApplicationContext context = application.run(args);
    HapiExitManager exitManager = context.getBean(HapiExitManager.class);
    int exitCode = exitManager.waitForExit(5);
    System.out.println("Exit code is: " + exitCode);
    System.exit(exitCode);
  }
}