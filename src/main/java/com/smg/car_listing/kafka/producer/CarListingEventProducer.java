package com.smg.car_listing.kafka.producer;

import com.smg.car_listing.kafka.events.CarListingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CarListingEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(CarListingEvent carListingEvent) {
        if (carListingEvent.getListingId() == null || carListingEvent.getListingId().isBlank()) {
            throw new IllegalArgumentException("listingId is required");
        }
        if (carListingEvent.getDateTime() == null) {
            carListingEvent.setDateTime(Instant.now());
        }

        kafkaTemplate.send("car-listings-events", carListingEvent.getListingId(), carListingEvent);
    }
}
