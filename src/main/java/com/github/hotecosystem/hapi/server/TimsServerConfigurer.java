package com.github.hotecosystem.hapi.server;

import ca.uhn.fhir.rest.server.RestfulServer;
import com.essaid.fhir.hapiext.Properties;
import com.essaid.fhir.hapiext.server.ServerConfigurer;
import com.essaid.fhir.hapiext.server.provider.ShutdownOperation;
import com.github.hotecosystem.hapi.server.provider.CSConceptTextSearchOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class TimsServerConfigurer implements ServerConfigurer {
    private final ApplicationContext applicationContext;
    private final Properties properties;

    public TimsServerConfigurer(ApplicationContext applicationContext,
                                Properties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Override
    public void configure(RestfulServer server) {
        server.registerProvider(applicationContext.getBean(
                CSConceptTextSearchOperation.class));
        server.registerProvider(applicationContext.getBean(ShutdownOperation.class));
    }
}
