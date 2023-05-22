package com.github.hotecosystem.hapi.em;

import com.essaid.fhir.hapi.ext.em.IEntityManagerFactoryBeanConfigurer;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class TimsEMBFConfigurer implements IEntityManagerFactoryBeanConfigurer {
    @Override
    public void configure(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {

    }
}
