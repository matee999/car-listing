package com.smg.car_listing.controller.mapper;

import com.smg.car_listing.controller.dto.CarListingDTO;
import com.smg.car_listing.es.model.CarListing;

public class Converters {

    public static CarListingDTO toCarListingDTO(CarListing entity) {
        CarListingDTO dto = new CarListingDTO();
        dto.setMake(entity.getMake());
        dto.setModel(entity.getModel());
        dto.setYear(entity.getYear());
        return dto;
    }
}
