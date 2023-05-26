package com.github.hotecosystem.hapi.impl;

import ca.uhn.fhir.jpa.entity.TermConcept;
import com.github.hotecosystem.hapi.api.TimsTerminologyService;
import com.github.hotecosystem.hapi.index.TimsMappingConfigurer;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TimsTerminologyServiceImpl implements TimsTerminologyService {

    private final EntityManager entityManager;

    public TimsTerminologyServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public List<TermConcept> findConceptsByDisplayOrDesignations(String searchString,
                                                                 boolean isSimpleQuery,
                                                                 List<String> onlyCodeSystems,
                                                                 List<String> preferredCodeSystems,
                                                                 List<String> exceptCodeSystems,
                                                                 Integer fetchStart,
                                                                 Integer fetchSize,
                                                                 boolean uniquifyCodeSystem) {
        SearchSession session = Search.session(entityManager);
        SearchScope<TermConcept> scope = session.scope(TermConcept.class);

        // CodeSystem handling

        // See https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/#search-dsl-predicate-terms
        SearchPredicate onlyCSPredicate = null;
        if (onlyCodeSystems != null && !onlyCodeSystems.isEmpty()) {
            onlyCSPredicate = scope.predicate().terms().field(TimsMappingConfigurer.CS_URL_FILED_NAME).matchingAny(onlyCodeSystems).toPredicate();
        }

        SearchPredicate preferredCSPredicate = null;
        if (preferredCodeSystems != null && !preferredCodeSystems.isEmpty()) {
            preferredCSPredicate = scope.predicate().terms().field(TimsMappingConfigurer.CS_URL_FILED_NAME).matchingAny(preferredCodeSystems).toPredicate();
        }

        SearchPredicate exceptCSPredicate = null;
        if (exceptCodeSystems != null && !exceptCodeSystems.isEmpty()) {
            exceptCSPredicate = scope.predicate().terms().field(TimsMappingConfigurer.CS_URL_FILED_NAME).matchingAny(exceptCodeSystems).toPredicate();
        }

        //  See https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/#search-dsl-predicate-boolean
        BooleanPredicateClausesStep<?> findPredicateStep = scope.predicate().bool();
        if (onlyCSPredicate != null) findPredicateStep.must(onlyCSPredicate);
        if (preferredCSPredicate != null) findPredicateStep.should(preferredCSPredicate);
        if (exceptCSPredicate != null) findPredicateStep.mustNot(exceptCSPredicate);


        // text search handling

        if (isSimpleQuery) {
            SearchPredicate predicate = scope.predicate().simpleQueryString().fields(TimsMappingConfigurer.DISPLAY_FILED_NAME, TimsMappingConfigurer.USE_VALUE_FILED_NAME)
                    .matching(searchString).toPredicate();
            findPredicateStep.must(predicate);
        } else {
            SearchPredicate predicate = scope.predicate().match().fields(TimsMappingConfigurer.DISPLAY_FILED_NAME, TimsMappingConfigurer.USE_VALUE_FILED_NAME)
                    .matching(searchString).toPredicate();
            findPredicateStep.must(predicate);
        }

        // results
        SearchResult<TermConcept> result = session.search(scope).where(findPredicateStep.toPredicate()).fetch(fetchStart, fetchSize);
        List<TermConcept> foundConcepts = result.hits();

        if (uniquifyCodeSystem) {
            Set<String> csConceptKey = new HashSet<>();
            foundConcepts = foundConcepts.stream().filter(c -> csConceptKey.add(c.getCodeSystemVersion().getCodeSystem().getCodeSystemUri() + "#" + c.getCode())).toList();
        }

        return foundConcepts;
    }
}
