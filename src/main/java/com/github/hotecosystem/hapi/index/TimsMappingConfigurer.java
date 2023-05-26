package com.github.hotecosystem.hapi.index;

import ca.uhn.fhir.jpa.entity.TermCodeSystemVersion;
import ca.uhn.fhir.jpa.entity.TermConcept;
import ca.uhn.fhir.jpa.entity.TermConceptDesignation;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmMappingConfigurationContext;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmSearchMappingConfigurer;
import org.hibernate.search.mapper.pojo.bridge.TypeBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.TypeBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.TypeBridgeWriteContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyMappingStep;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.TypeMappingStep;
import org.springframework.stereotype.Component;

@Component
public class TimsMappingConfigurer implements HibernateOrmSearchMappingConfigurer {

    public static final String CS_URL_FILED_NAME = "csUrl";
    public static final String DISPLAY_FILED_NAME = "myDisplay";
    public static final String USE_VALUE_FILED_NAME = "designations.myValue";


    @Override
    public void configure(HibernateOrmMappingConfigurationContext context) {

        // Term concept
        TypeMappingStep termConceptType = context.programmaticMapping().type(TermConcept.class);
        termConceptType.binder(new TermConceptBinder());
        PropertyMappingStep propStep = termConceptType.property("myDesignations");
        propStep.indexedEmbedded("designations").structure(ObjectStructure.NESTED);


        // Designations
        TypeMappingStep designationType = context.programmaticMapping().type(TermConceptDesignation.class);
        designationType.property("myLanguage").genericField().projectable(Projectable.YES);
        designationType.property("myUseSystem").genericField();
        designationType.property("myUseCode").genericField().projectable(Projectable.YES);
        designationType.property("myUseDisplay").genericField();


        designationType.property("myValue").fullTextField()
                .analyzer("standardAnalyzer");

//        designationType.property("myValue").fullTextField("myValueEdgeNGram").
//                analyzer("autocompleteEdgeAnalyzer").projectable(Projectable.NO);
//
//        designationType.property("myValue").fullTextField("myValueWordEdgeNGram")
//                .analyzer("autocompleteWordEdgeAnalyzer").projectable(Projectable.NO);
//
//        designationType.property("myValue").fullTextField("myValueNGram")
//                .analyzer("autocompleteNGramAnalyzer").projectable(Projectable.NO);
//
//        designationType.property("myValue").fullTextField("myValuePhonetic")
//                .analyzer("autocompletePhoneticAnalyzer").projectable(Projectable.NO);
    }

    private static class TermConceptBinder implements TypeBinder {

        private IndexFieldReference<String> csUrlFieldRef;
        private IndexFieldReference<String> csVersionFieldRef;

        @Override
        public void bind(TypeBindingContext context) {

            context.dependencies().useRootOnly();
            csUrlFieldRef = context.indexSchemaElement()
                    .field(CS_URL_FILED_NAME, f -> f.asString().analyzer("keyword")).toReference();
            csVersionFieldRef = context.indexSchemaElement()
                    .field("csVersion", f -> f.asString().analyzer("keyword")).toReference();

            context.bridge(TermConcept.class, new TermConceptBridge());
        }

        private class TermConceptBridge implements TypeBridge<TermConcept> {

            @Override
            public void write(DocumentElement target, TermConcept termConcept, TypeBridgeWriteContext context) {
                TermCodeSystemVersion codeSystemVersion = termConcept.getCodeSystemVersion();
                target.addValue(csUrlFieldRef,
                        codeSystemVersion.getCodeSystem().getCodeSystemUri());
                target.addValue(csVersionFieldRef,
                        codeSystemVersion.getCodeSystemVersionId());
            }
        }
    }


}
