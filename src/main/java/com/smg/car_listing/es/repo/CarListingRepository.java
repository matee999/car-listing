package com.smg.car_listing.es.repo;

import com.smg.car_listing.es.model.CarListing;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarListingRepository extends ElasticsearchRepository<CarListing, String> {}
