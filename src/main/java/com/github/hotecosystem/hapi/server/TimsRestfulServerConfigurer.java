package com.github.hotecosystem.hapi.server;

import ca.uhn.fhir.rest.server.RestfulServer;
import com.essaid.fhir.hapi.ext.HapiExtensionProperties;
import com.essaid.fhir.hapi.ext.server.IRestfulServerConfigurer;
import com.essaid.fhir.hapi.ext.server.provider.ShutdownOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class TimsRestfulServerConfigurer implements IRestfulServerConfigurer {
    private final ApplicationContext applicationContext;
    private final HapiExtensionProperties properties;

    public TimsRestfulServerConfigurer(ApplicationContext applicationContext, HapiExtensionProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Override
    public void configure(RestfulServer server) {
        server.registerProvider(applicationContext.getBean(CSConceptSearchOperation.class));
        server.registerProvider(applicationContext.getBean(ShutdownOperation.class));
    }
}
