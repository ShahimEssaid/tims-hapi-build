package com.github.hotecosystem.hapi.server.provider;

import ca.uhn.fhir.jpa.entity.TermConcept;
import ca.uhn.fhir.jpa.entity.TermConceptDesignation;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.github.hotecosystem.hapi.api.TimsTerminologyService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Component
@Transactional
public class CSConceptTextSearchOperation implements IResourceProvider {

    private final EntityManager entityManager;
    private final TimsTerminologyService timsTerminologyService;


    public CSConceptTextSearchOperation(EntityManager entityManager, TimsTerminologyService terminologyService) {
        this.entityManager = entityManager;
        this.timsTerminologyService = terminologyService;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CodeSystem.class;
    }

    @Operation(name = "text-search", idempotent = true)
    public Parameters textSearch(
            @OperationParam(name = "search-string", typeName = "string", min = 1, max = 1) IPrimitiveType<String> searchString,
            @OperationParam(name = "is-simple-query", typeName = "boolean") IPrimitiveType<Boolean> isSimpleQuery,
            @OperationParam(name = "only-system", typeName = "string") List<IPrimitiveType<String>> onlyCodeSystems,
            @OperationParam(name = "prefer-system", typeName = "string") List<IPrimitiveType<String>> preferredCodeSystems,
            @OperationParam(name = "except-system", typeName = "string") List<IPrimitiveType<String>> exceptCodeSystems,
            @OperationParam(name = "fetch-start", typeName = "integer") IPrimitiveType<Integer> fetchStart,
            @OperationParam(name = "fetch-size", typeName = "integer") IPrimitiveType<Integer> fetchSize,
            @OperationParam(name = "unique-system", typeName = "boolean") IPrimitiveType<Boolean> uniqueSystem
    ) {

        boolean isSimple = isSimpleQuery != null && isSimpleQuery.getValue();
        List<String> onlySystems = onlyCodeSystems == null ? Collections.emptyList() : onlyCodeSystems.stream().map(IPrimitiveType::getValueAsString).toList();
        List<String> preferredSystems = preferredCodeSystems == null ? Collections.emptyList() : preferredCodeSystems.stream().map(IPrimitiveType::getValueAsString).toList();
        List<String> exceptSystems = exceptCodeSystems == null ? Collections.emptyList() : exceptCodeSystems.stream().map(IPrimitiveType::getValueAsString).toList();
        int start = fetchStart == null ? 0 : fetchStart.getValue();
        int size = fetchSize == null ? 25 : fetchSize.getValue();

        List<TermConcept> concepts = timsTerminologyService.findConceptsByDisplayOrDesignations(searchString.getValue(), isSimple, onlySystems, preferredSystems, exceptSystems, start, size, false);

        int counter = 0;
        Parameters parameters = new Parameters();
        for (TermConcept termConcept : concepts) {
//            System.out.println("Count: " + ++counter + " -- " + termConcept);
            Parameters.ParametersParameterComponent matchParameter = parameters.addParameter();
            matchParameter.setName("match");
            matchParameter.addPart().setName("system").setValue(new StringType(termConcept.getCodeSystemVersion().getCodeSystem().getCodeSystemUri()));
            matchParameter.addPart().setName("code").setValue(new StringType(termConcept.getCode()));
            matchParameter.addPart().setName("display").setValue(new StringType(termConcept.getDisplay()));
            for (TermConceptDesignation designation : termConcept.getDesignations()) {
                matchParameter.addPart().setName("designation")
                        .setValue(new StringType(designation.getValue() + " useSystem:" + designation.getUseSystem() + " useCode:" + designation.getUseCode() + " useDisplay:" + designation.getUseDisplay() + " language:" + designation.getLanguage()));
            }
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
