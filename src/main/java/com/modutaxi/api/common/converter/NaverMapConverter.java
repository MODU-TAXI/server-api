package com.modutaxi.api.common.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("commonConverter")
public class NaverMapConverter {

    private static final int LONGITUDE = 0;
    private static final int LATITUDE = 1;

    public static String coordinateToString(double longitude, double latitude) {
        return String.format("%.6f, %.6f", longitude, latitude);
    }

    public static LineString jsonNodeToLineString(JsonNode pathNode) {
        List<Position> path = new ArrayList<>();
        for (JsonNode pointNode : pathNode) {
            double longitude = pointNode.get(LONGITUDE).asDouble();
            double latitude = pointNode.get(LATITUDE).asDouble();
            Position position = new Position(longitude, latitude);
            path.add(position);
        }
        return new LineString(path);
    }
}
