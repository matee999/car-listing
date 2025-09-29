package com.smg.car_listing;

import org.springframework.boot.SpringApplication;

public class TestCarListingApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarListingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
