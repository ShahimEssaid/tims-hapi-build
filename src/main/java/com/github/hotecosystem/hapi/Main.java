package com.github.hotecosystem.hapi;

import ca.uhn.fhir.jpa.starter.Application;
import com.essaid.fhir.hapi.ext.HapiExtensionConfiguration;
import com.essaid.fhir.hapi.ext.server.HapiExitManager;
import com.essaid.fhir.hapi.ext.index.NullIndexAnalysisConfigurer;
import com.essaid.fhir.hapi.ext.server.provider.ShutdownOperation;
import com.github.hotecosystem.hapi.server.TimsRestfulServerConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackageClasses = {TimsRestfulServerConfigurer.class, NullIndexAnalysisConfigurer.class})
@Import({Application.class, HapiExtensionConfiguration.class, ShutdownOperation.class, HapiExitManager.class})
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