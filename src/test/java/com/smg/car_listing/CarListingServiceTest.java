package com.smg.car_listing;

import com.smg.car_listing.controller.dto.CarListingDTO;
import com.smg.car_listing.es.model.CarListing;
import com.smg.car_listing.es.repo.CarListingRepository;
import com.smg.car_listing.es.service.CarListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class CarListingServiceTest {

    @Autowired
    private CarListingService service;
    @Autowired
    private CarListingRepository repo;
    @Autowired
    private ElasticsearchOperations operations;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        repo.saveAll(List.of(
                carListing("1", "Toyota", "Corolla", 2018),
                carListing("2", "Toyota", "Corolla", 2019),
                carListing("3", "Toyota", "Corolla", 2020),
                carListing("4", "Toyota", "Corolla", 2021),
                carListing("5", "Toyota", "Yaris",   2020),
                carListing("6", "Honda",  "Civic",   2020),
                carListing("7", "Toyota", "Corolla", 2022)));

        IndexOperations indexOps = operations.indexOps(CarListing.class);
        indexOps.refresh();
    }

    private static CarListing carListing(String id, String make, String model, Integer year) {
        CarListing c = new CarListing();
        c.setId(id);
        c.setMake(make);
        c.setModel(model);
        c.setYear(year);
        return c;
    }

    @Test
    void searchFiltersCountAndSortDesc() {
        Page<CarListingDTO> page = service.search(
                "Toyota", "Corolla",
                2019, 2021,
                0, 10,
                "-year");

        assertEquals(3, page.getTotalElements());

        // check sort
        var years = page.getContent().stream().map(CarListingDTO::getYear).toList();
        assertEquals(List.of(2021, 2020, 2019), years);
    }
}