package com.smg.car_listing.kafka.events;

import lombok.Data;

@Data
public abstract class KafkaMessage {
    private String eventId;
    private KafkaMessageType type;
    private Long schemaVersion;
}
