package com.smg.car_listing;

import com.smg.car_listing.es.repo.CarListingRepository;
import com.smg.car_listing.kafka.events.CarListingEvent;
import com.smg.car_listing.kafka.events.KafkaMessageType;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class CarListingEventListenerTest {

    private static final String TOPIC = "car-listings-events";

    @Autowired
    KafkaTemplate<String, CarListingEvent> kafkaTemplate;
    @Autowired
    CarListingRepository repo;

    @Autowired
    Environment env;

    private CarListingEvent event(String id, KafkaMessageType type, Long version, String make, String model, Integer year) {
        var carListingEvent = new CarListingEvent();
        carListingEvent.setEventId(UUID.randomUUID().toString());
        carListingEvent.setType(type);
        carListingEvent.setSchemaVersion(1L);
        carListingEvent.setListingId(id);
        carListingEvent.setVersion(version);
        carListingEvent.setMake(make);
        carListingEvent.setModel(model);
        carListingEvent.setYear(year);
        carListingEvent.setDateTime(Instant.now());
        return carListingEvent;
    }

    private void send(String topic, String key, CarListingEvent event) {
        RecordMetadata recordMetadata =
                kafkaTemplate.send(topic, key, event).toCompletableFuture().join()
                        .getRecordMetadata();
        //        kafkaTemplate.send(topic, key, event);
        kafkaTemplate.flush();
        System.out.println("Event sent to topic: " + recordMetadata.topic());
    }

    private void awaitFound(String id, int expectedYear) {
        System.out.println("Assertation of result: " + id);
        await().pollDelay(Duration.ofMillis(300))
                .pollInterval(Duration.ofMillis(200))
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    var found = repo.findById(id).orElseThrow();
                    assertEquals(expectedYear, found.getYear());
                });
    }

    @Test
    void contextLoads() {
        System.out.println("test");
    }

    @Test
    void printBootstrap() {
        System.out.println("BOOTSTRAP = " + env.getProperty("spring.kafka.bootstrap-servers"));
    }

    @Test
    void createCarListingDocument() {
        String id = UUID.randomUUID().toString();
        send(TOPIC, id, event(id, KafkaMessageType.CREATE, 1L, "Toyota", "Corolla", 2020));

        awaitFound(id, 2020);
    }

    @Test
    void updateExistingCarListingDocument() {
        String id = UUID.randomUUID().toString();

        // arrange
        send(TOPIC, id, event(id, KafkaMessageType.CREATE, 1L, "Toyota", "Corolla", 2020));

        // act
        send(TOPIC, id, event(id, KafkaMessageType.UPDATE, 2L, "Toyota", "Corolla", 2021));

        // assert
        awaitFound(id, 2021);
    }

    @Test
    void updateWithOldVersion() {
        String id = UUID.randomUUID().toString();

        //create
        send(TOPIC, id, event(id, KafkaMessageType.CREATE, 1L, "Toyota", "Corolla", 2020));

        // update v2
        send(TOPIC, id, event(id, KafkaMessageType.UPDATE, 2L, "Toyota", "Corolla", 2021));

        // update v1
        send(TOPIC, id, event(id, KafkaMessageType.UPDATE, 1L, "Toyota", "Corolla", 2019));

        // assert
        awaitFound(id, 2021);
    }

    @Test
    void createUpdateIgnoreOldDelete(){
        String id = UUID.randomUUID().toString();

        send(TOPIC, id, event(id, KafkaMessageType.CREATE, 1L, "Toyota", "Corolla", 2020));
        awaitFound(id, 2020);

        // update
        send(TOPIC, id, event(id, KafkaMessageType.UPDATE, 2L, "Toyota", "Corolla", 2021));
        awaitFound(id, 2021);

        // update with version 1
        send(TOPIC, id, event(id, KafkaMessageType.UPDATE, 1L, "Toyota", "Corolla", 2019));
        awaitFound(id, 2021);

        // delete
        send(TOPIC, id, event(id, KafkaMessageType.DELETE, 3L, null, null, null));
        await().pollDelay(Duration.ofMillis(200)).atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertTrue(repo.findById(id).isEmpty()));
    }
}
