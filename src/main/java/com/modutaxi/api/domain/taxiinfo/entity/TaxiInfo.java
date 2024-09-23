package com.modutaxi.api.domain.taxiinfo.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaxiInfo {

    @Id
    private Long id;
    @Column(columnDefinition = "mediumtext")
    private String path;

    public LineString getPath() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SimpleModule()
                .addDeserializer(LineString.class, new LineStringDeserializer()));
        try {
            return objectMapper.readValue(path, LineString.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TaxiInfo toEntity(Long id, LineString path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return TaxiInfo.builder()
                .id(id)
                .path(objectMapper.writeValueAsString(path))
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class LineStringDeserializer extends JsonDeserializer<LineString> {
        @Override
        public LineString deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            JsonNode coordinatesNode = node.get("coordinates");
            List<Position> coordinates = new ArrayList<>();

            if (coordinatesNode != null && coordinatesNode.isArray()) {
                for (JsonNode coordNode : coordinatesNode) {
                    Position position = new PositionDeserializer().deserialize(coordNode.traverse(p.getCodec()), ctxt);
                    coordinates.add(position);
                }
            }
            return new LineString(coordinates);
        }
    }

    private class PositionDeserializer extends JsonDeserializer<Position> {
        @Override
        public Position deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            JsonNode valuesNode = node.get("values");
            List<Double> values = new ArrayList<>();

            if (valuesNode != null && valuesNode.isArray()) {
                for (JsonNode valueNode : valuesNode) {
                    if (valueNode.isDouble()) {
                        values.add(valueNode.asDouble());
                    }
                }
            }
            return new Position(values);
        }
    }
}
