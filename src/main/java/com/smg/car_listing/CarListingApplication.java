package com.smg.car_listing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableCaching
@SpringBootApplication
public class CarListingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarListingApplication.class, args);
	}

}
