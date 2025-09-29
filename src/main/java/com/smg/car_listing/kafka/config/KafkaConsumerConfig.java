package com.smg.car_listing.kafka.config;

import java.util.Map;
import com.smg.car_listing.kafka.events.CarListingEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

//@Configuration
//public class KafkaConsumerConfig {
//
//    @Bean
//    public ConsumerFactory<String, CarListingEvent> carListingConsumerFactory(KafkaProperties kafkaProps) {
//        Map<String, Object> cfg = kafkaProps.buildConsumerProperties();
//
//        JsonDeserializer<CarListingEvent> valueDeserializer = new JsonDeserializer<>(CarListingEvent.class);
//        valueDeserializer.addTrustedPackages("*");
//
//        return new DefaultKafkaConsumerFactory<>(
//                cfg,
//                new StringDeserializer(),
//                valueDeserializer
//        );
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, CarListingEvent> kafkaListenerContainerFactory(
//            ConsumerFactory<String, CarListingEvent> carListingConsumerFactory) {
//        var factory = new ConcurrentKafkaListenerContainerFactory<String, CarListingEvent>();
//        factory.setConsumerFactory(carListingConsumerFactory);
//        factory.setConcurrency(1);
//
//        return factory;
//    }
//}

