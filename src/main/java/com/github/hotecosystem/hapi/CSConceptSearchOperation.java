package com.github.hotecosystem.hapi;

import ca.uhn.fhir.jpa.entity.TermConcept;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@Transactional
public class CSConceptSearchOperation implements IResourceProvider {

    private final EntityManager entityManager;


    public CSConceptSearchOperation(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CodeSystem.class;
    }

    @Operation(name = "concept-search", idempotent = true)
    public Parameters conceptSearch(@OperationParam(name = "text", typeName = "string", min = 1, max = 1) IPrimitiveType<String> text) {
        SearchSession searchSession = Search.session(entityManager);
        SearchResult<TermConcept> concepts = searchSession
                .search(TermConcept.class)
                .where(f -> f.match().field("myDisplay").matching(text.getValue()))
                .sort(SearchSortFactory::score)
                .fetch(10000);

        int counter = 0;
        Parameters parameters = new Parameters();
        for (TermConcept termConcept : concepts.hits()) {
//            System.out.println("Count: " + ++counter + " -- " + termConcept);
            parameters
                    .addParameter()
                    .setName("concept")
                    .setValue(new StringType(termConcept.getCode()))
                    .addPart()
                    .setName("display")
                    .setValue(new StringType(termConcept.getDisplay()));
        }

        return parameters;
    }


//    @Operation(name = "concept-search", idempotent = true)
////    public Parameters conceptSearch(@IdParam IdType id) {
////        Parameters parameters = new Parameters();
////        parameters.addParameter().setName("concept-search").setValue(new StringType("concept-value: " + id));
////        return parameters;
////    }
}
