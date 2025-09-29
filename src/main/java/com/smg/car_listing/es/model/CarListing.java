package com.smg.car_listing.es.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.Instant;

@Data
@Document(indexName = "car-listings", versionType = Document.VersionType.EXTERNAL)
public class CarListing {
    @Id
    private String id;
    private String make;
    private String model;
    private Integer year;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant updatedAt;
    @Version
    private Long version;
}
