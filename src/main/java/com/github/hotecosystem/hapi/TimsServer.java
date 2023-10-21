package com.github.hotecosystem.hapi;

import ca.uhn.fhir.jpa.starter.Application;
import com.essaid.fhir.hapiext.server.ExitManager;
import com.essaid.fhir.hapiext.server.provider.ShutdownOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import java.io.IOException;

@SpringBootApplication()
@Import({Application.class, ShutdownOperation.class, ExitManager.class})
public class TimsServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        SpringApplication application = new SpringApplication(TimsServer.class);
        ConfigurableApplicationContext context = application.run(args);
        ExitManager exitManager = context.getBean(ExitManager.class);
        int exitCode = exitManager.waitForExit(5);
        System.out.println("Exit code is: " + exitCode);
        System.exit(exitCode);
    }
}