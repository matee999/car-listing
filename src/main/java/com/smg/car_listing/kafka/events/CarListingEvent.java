package com.smg.car_listing.kafka.events;

import lombok.Data;

import java.time.Instant;

@Data
public class CarListingEvent extends KafkaMessage {
    private String listingId;
    private String make;
    private String model;
    private Integer year;
    private Instant dateTime;

    private Long version;
}
