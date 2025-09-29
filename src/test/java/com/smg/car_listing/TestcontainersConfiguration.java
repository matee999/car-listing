package com.smg.car_listing;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	ElasticsearchContainer elasticsearchContainer() {
		ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
				DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.19.4"))
				.withEnv("xpack.security.enabled", "false");

		return elasticsearchContainer;
	}

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
	}

	@Bean
	NewTopic carListingsEvents() {
		return TopicBuilder.name("car-listings-events").partitions(1).replicas(1).build();
	}

}
