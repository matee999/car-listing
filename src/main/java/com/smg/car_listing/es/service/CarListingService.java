package com.smg.car_listing.es.service;

import com.smg.car_listing.controller.dto.CarListingDTO;
import com.smg.car_listing.controller.mapper.Converters;
import com.smg.car_listing.es.model.CarListing;
import com.smg.car_listing.es.query.CarListingQueryBuilder;
import com.smg.car_listing.es.repo.CarListingRepository;
import com.smg.car_listing.kafka.events.CarListingEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.VersionConflictException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CarListingService {

    private final CarListingRepository repo;
    private final ElasticsearchOperations operations;

    @CacheEvict(value = "carListingsSearch", allEntries = true)
    public void updateOrInsertFromEvent(CarListingEvent carListingEvent) {
        if (carListingEvent.getListingId() == null || carListingEvent.getListingId().isBlank()) {
            log.warn("Missing listingId in event {}", carListingEvent);
            return;
        }

        if (carListingEvent.getVersion() == null) {
            log.warn("Missing version in event with listingId={}", carListingEvent.getListingId());
            return;
        }

        CarListing doc = repo.findById(carListingEvent.getListingId()).orElseGet(() -> {
            CarListing n = new CarListing();
            n.setId(carListingEvent.getListingId());
            return n;
        });

        if (carListingEvent.getMake() != null) doc.setMake(carListingEvent.getMake());
        if (carListingEvent.getModel() != null) doc.setModel(carListingEvent.getModel());
        if (carListingEvent.getYear() != null) doc.setYear(carListingEvent.getYear());
        if (carListingEvent.getDateTime() != null) doc.setUpdatedAt(carListingEvent.getDateTime());

        doc.setVersion(carListingEvent.getVersion());

        try {
            repo.save(doc);
            log.info("Created or updated listing id={}", carListingEvent.getListingId());
        } catch (VersionConflictException ex) {
            log.info("Ignored older event (version conflict) id={}, version={}",
                    carListingEvent.getListingId(), carListingEvent.getVersion());
        }
    }

    @CacheEvict(value = "carListingsSearch", allEntries = true)
    public void deleteById(String listingId) {
        if (listingId == null || listingId.isBlank()) {
            log.warn("Missing listingId");
            return;
        }
        repo.deleteById(listingId);
        log.info("Deleted listing with id={}", listingId);
    }

    @Cacheable(
            value = "carListingsSearch",
            key = "T(java.util.Objects).hash(#make, #model, #yearFrom, #yearTo, #page, #size, #sort)"
    )
    public Page<CarListingDTO> search(String make, String model, Integer yearFrom, Integer yearTo,
            Integer page, Integer size, String sort) {
        log.info("Search car listing by make={}, model={}, yearFrom={}, yearTo={}, page={}, size={}, sort={}",
                make, model, yearFrom, yearTo, page, size, sort);
        var query = CarListingQueryBuilder.builder()
                .make(make)
                .model(model)
                .yearBetween(yearFrom, yearTo)
                .pageable(PageRequest.of(page, size))
                .sortBy(sort)
                .build();

        var carListingSearchHits = operations.search(query, CarListing.class);

        var dtos = carListingSearchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(Converters::toCarListingDTO)
                .toList();

        return new PageImpl<>(dtos, query.getPageable(), carListingSearchHits.getTotalHits());
    }
}
