package com.smg.car_listing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smg.car_listing.controller.dto.CarListingDTO;
import com.smg.car_listing.es.service.CarListingService;
import com.smg.car_listing.kafka.events.KafkaMessageType;
import com.smg.car_listing.kafka.producer.CarListingEventProducer;
import com.smg.car_listing.kafka.events.CarListingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car-listing")
public class CarListingController {
    private final CarListingEventProducer producer;
    private final CarListingService service;
    private final ObjectMapper objectMapper;

    @PostMapping("/event")
    public ResponseEntity<Void> send(@RequestBody CarListingEvent evt) {
        evt.setEventId(UUID.randomUUID().toString());
        producer.publish(evt);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/search")
    public Page<CarListingDTO> search(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return service.search(make, model, yearFrom, yearTo, page, size, sort);
    }

    @PostMapping("/file")
    public ResponseEntity<String> publishedFromFile(
            @RequestParam(defaultValue = "car_listings_500.json") String path) {
        try {
            Resource res = new ClassPathResource(path);
            if (!res.exists()) {
                return ResponseEntity.badRequest().body("Resource not found on classpath: " + path);
            }

            List<CarListingDTO> cars;
            try (InputStream is = res.getInputStream()) {
                cars = objectMapper.readValue(is, new TypeReference<List<CarListingDTO>>() {});
            }

            for (CarListingDTO r : cars) {
                CarListingEvent evt = new CarListingEvent();
                evt.setEventId(UUID.randomUUID().toString());
                evt.setType(KafkaMessageType.CREATE);
                evt.setSchemaVersion(1L);

                evt.setListingId(UUID.randomUUID().toString());
                evt.setMake(r.getMake());
                evt.setModel(r.getModel());
                evt.setYear(r.getYear());
                evt.setVersion(0L);

                producer.publish(evt);
            }

            return ResponseEntity.ok("Successfully published from file: " + path);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to published from file: " + e.getMessage());
        }
    }
}
