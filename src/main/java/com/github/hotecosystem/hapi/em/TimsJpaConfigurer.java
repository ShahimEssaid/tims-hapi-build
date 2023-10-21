package com.github.hotecosystem.hapi.em;

import com.essaid.fhir.hapiext.jpa.JpaConfigurer;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class TimsJpaConfigurer implements JpaConfigurer {
    @Override
    public void configure(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {

    }
}
