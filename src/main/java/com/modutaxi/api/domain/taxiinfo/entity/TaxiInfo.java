package com.modutaxi.api.domain.taxiinfo.entity;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "taxi-info")
public class TaxiInfo implements Serializable {

    @Id
    private Long id;

    private List<Point> path;

    public static TaxiInfo toEntity(Long id, List<Point> path) {
        return TaxiInfo.builder()
            .id(id)
            .path(path)
            .build();
    }
}
