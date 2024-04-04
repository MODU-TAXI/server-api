package com.modutaxi.api.domain.taxiinfo.entity;

import com.mongodb.client.model.geojson.LineString;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "taxi-info")
public class TaxiInfo implements Serializable {

    @Id
    private Long id;

    private LineString path;

    public static TaxiInfo toEntity(Long id, LineString path) {
        return TaxiInfo.builder()
            .id(id)
            .path(path)
            .build();
    }
}
