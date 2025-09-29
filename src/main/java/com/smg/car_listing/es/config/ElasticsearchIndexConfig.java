package com.smg.car_listing.es.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.document.Document;
import java.io.IOException;

@Configuration
public class ElasticsearchIndexConfig {

    public static final String INDEX_NAME = "car-listings";

    @Bean
    public boolean createCarListingsIndex(ElasticsearchOperations operations) throws IOException {
        IndexOperations indexOps = operations.indexOps(IndexCoordinates.of(INDEX_NAME));

        String mappingJson = readClasspath("es/car-listings-mapping.json");

        if (!indexOps.exists()) {
            String settingsJson = readClasspath("es/car-listings-settings.json");

            indexOps.create(Document.parse(settingsJson));
            indexOps.putMapping(Document.parse(mappingJson));
            indexOps.refresh();
            return true;
        }

        try {
            indexOps.putMapping(Document.parse(mappingJson));
        } catch (Exception ex) {
            System.out.println("Error Creating Index!!!" + ex.getMessage());
        }

        System.out.println("Index Created!!!");
        return true;
    }

    private String readClasspath(String path) throws IOException {
        ClassPathResource res = new ClassPathResource(path);
        try (var in = res.getInputStream()) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
