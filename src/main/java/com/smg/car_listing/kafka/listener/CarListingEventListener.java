package com.smg.car_listing.kafka.listener;

import com.smg.car_listing.es.service.CarListingService;
import com.smg.car_listing.kafka.events.CarListingEvent;
import com.smg.car_listing.kafka.events.KafkaMessageType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CarListingEventListener {

    private final CarListingService service;

    @KafkaListener(
            topics = "car-listings-events",
            groupId = "listings-projection",
//            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "10")
    public void onMessage(@Payload CarListingEvent event, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received event: type={}, id={}", event.getType(), event.getListingId());

        if (key == null || !key.equals(event.getListingId())) {
            log.warn("Kafka key is not equals as listing id! Key={}, listingId={})", key, event.getListingId());
        }

        switch (event.getType()) {
            case KafkaMessageType.CREATE, KafkaMessageType.UPDATE -> service.updateOrInsertFromEvent(event);
            case KafkaMessageType.DELETE -> service.deleteById(event.getListingId());
            default -> log.warn("Unknown event type: {}", event.getType());
        }
    }
}
