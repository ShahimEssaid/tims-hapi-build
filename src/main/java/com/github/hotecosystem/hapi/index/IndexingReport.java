package com.github.hotecosystem.hapi.index;

import org.hibernate.search.engine.backend.metamodel.IndexDescriptor;
import org.hibernate.search.engine.backend.metamodel.IndexFieldDescriptor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.entity.SearchIndexedEntity;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
@Profile("off")
public class IndexingReport {

    private final EntityManagerFactory emf;

    public IndexingReport(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @EventListener({ApplicationReadyEvent.class})
    public void printReport(ApplicationReadyEvent event) {
        StringBuilder sb = new StringBuilder();
        SearchMapping mapping = Search.mapping(emf);

        Collection<? extends SearchIndexedEntity<?>> indexedEntities = mapping.allIndexedEntities();

        for (SearchIndexedEntity<?> indexedEntity : indexedEntities) {
            IndexDescriptor descriptor = indexedEntity.indexManager().descriptor();
            sb.append("Indexed entity: " + indexedEntity + ", descriptor: " + descriptor + "\n");
            ArrayList<IndexFieldDescriptor> indexFieldDescriptors = new ArrayList<>(descriptor.staticFields());
            Collections.sort(indexFieldDescriptors, (o1, o2) -> o1.absolutePath().compareTo(o2.absolutePath()));
            for (IndexFieldDescriptor field : indexFieldDescriptors) {
                sb.append("\t" + field.toString() + "\n");
            }
        }
        System.out.println(sb.toString());
    }
}
