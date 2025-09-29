package com.smg.car_listing.es.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.util.ArrayList;
import java.util.List;

public class CarListingQueryBuilder {

    private String make;
    private String model;
    private Integer yearFrom;
    private Integer yearTo;

    private Pageable pageable = PageRequest.of(0, 20);
    private Sort sort = Sort.by(Sort.Order.desc("updatedAt"));

    public static CarListingQueryBuilder builder() {
        return new CarListingQueryBuilder();
    }

    public CarListingQueryBuilder make(String make) {
        if (make != null) {
            this.make = make;
        }
        return this;
    }

    public CarListingQueryBuilder model(String model) {
        if (model != null) {
            this.model = model;
        }
        return this;
    }

    public CarListingQueryBuilder yearBetween(Integer from, Integer to) {
        this.yearFrom = from;
        this.yearTo = to;
        return this;
    }

    public CarListingQueryBuilder pageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public CarListingQueryBuilder sortBy(String sortField) {
        if (sortField != null) {
            String sort = sortField.trim();
            if (sort.startsWith("-")) {
                this.sort = Sort.by(Sort.Order.desc(sort.substring(1)));
                return this;
            }
            this.sort = Sort.by(Sort.Order.asc(sort));
        }

        return this;
    }

    public NativeQuery build() {
        List<Query> must = new ArrayList<>();
        List<Query> filter = new ArrayList<>();

        if (make != null) {
            must.add(Query.of(q -> q.match(m -> m.field("make").query(make))));
        }
        if (model != null) {
            must.add(Query.of(q -> q.match(m -> m.field("model").query(model))));
        }

        if (yearFrom != null || yearTo != null) {
            filter.add(Query.of(q -> q.range(r -> r.number(n -> {
                n.field("year");
                if (yearFrom != null) {
                    n.gte(Double.valueOf(yearFrom));
                }
                if (yearTo != null) {
                    n.lte(Double.valueOf(yearTo));
                }
                return n;
            }))));
        }

        Query bool = Query.of(q -> q.bool(b -> {
            if (!must.isEmpty())   b.must(must);        // TODO - use *text*
            if (!filter.isEmpty()) b.filter(filter);
            return b;
        }));

        return NativeQuery.builder()
                .withQuery(bool)
                .withPageable(pageable)
                .withSort(sort)
                .withTrackTotalHits(true)
                .build();
    }
}
