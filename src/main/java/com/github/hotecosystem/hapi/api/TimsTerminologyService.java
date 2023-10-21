package com.github.hotecosystem.hapi.api;

import ca.uhn.fhir.jpa.entity.TermConcept;

import java.util.List;

public interface TimsTerminologyService {

    List<TermConcept> findConceptsByDisplayOrDesignations(
            String searchString,
            boolean isSimpleQuery,
            List<String> onlyCodeSystems,
            List<String> preferredCodeSystems,
            List<String> exceptCodeSystems,
            Integer fetchStart,
            Integer fetchSize,
            boolean uniquifyCodeSystem);
}
