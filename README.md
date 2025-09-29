# car-listing

## Prerequisites

* JDK 21+
* Gradle (wrapper included: ./gradlew)
* Docker running (Docker Desktop or daemon).

Tests use Testcontainers to spin up Kafka and Elasticsearch automatically—no local installs or fixed ports required.

## Run all tests
`./gradlew clean test`

## Run a single test class

### Consumer + Elasticsearch save/update/delete handling
`./gradlew test --tests "com.smg.car_listing.CarListingEventListenerTest"`

### Search functionality test Elasticsearch
`./gradlew test --tests "com.smg.car_listing.CarListingServiceTest"`