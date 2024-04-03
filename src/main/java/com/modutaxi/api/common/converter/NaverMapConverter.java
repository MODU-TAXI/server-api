package com.modutaxi.api.common.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("commonConverter")
public class NaverMapConverter {
    public static String coordinateToString(float longitude, float latitude) {
        return String.format("%.6f, %.6f", longitude, latitude);
    }

    public static List<Point> jsonNodeToPointList(JsonNode pathNode){
        List<Point> path = new ArrayList<>();
        for (JsonNode pointNode : pathNode) {
            float latitude = (float) pointNode.get(1).asDouble();
            float longitude = (float) pointNode.get(0).asDouble();
            Point point = new Point(latitude, longitude);
            path.add(point);
        }
        return path;
    }
}
