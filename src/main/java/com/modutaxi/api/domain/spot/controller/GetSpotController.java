package com.modutaxi.api.domain.spot.controller;


import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.spot.dto.SpotRequestDto.GetAreaSpotRequest;
import com.modutaxi.api.domain.spot.dto.SpotRequestDto.SearchSpotPointRequest;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.GetSpotResponses;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.GetSpotWithDistanceResponse;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.SearchWithRadiusResponses;
import com.modutaxi.api.domain.spot.service.GetSpotService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.modutaxi.api.domain.spot.dto.SpotRequestDto.CurrentPointRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
@Tag(name = "거점 조회", description = "거점 조회 API")
public class GetSpotController {
    private final GetSpotService getSpotService;

    @PostMapping("/{id}")
    @Operation(
            summary = "특정 거점 조회",
            description = "거점 데이터를 조회합니다.<br>조회하려는 거점의 id와 사용자 기기의 현재 위치(x:경도, y:위도)를 입력해주세요.<br>거점까지의 거리(m)를 계산하여 함께 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetSpotWithDistanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "거점 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "SPOT_001", value = """
                            {
                                "code": "SPOT_001",
                                "message": "존재하지 않는 거점 ID 입니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "일치하는 ID를 가진 거점이 존재하지 않습니다."),
            })),
    })
    public ResponseEntity<GetSpotWithDistanceResponse> getSpot(
            @Parameter(description = "거점 id")
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CurrentPointRequest.class)), description = "현재 위치")
            @RequestBody CurrentPointRequest request
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(request.getCurrentPoint().getX(), request.getCurrentPoint().getY());
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(getSpotService.getSpot(id, point));
    }

    @Hidden
    @PostMapping("/area")
    @Operation(
            summary = "영역 내 거점 조회",
            description = "영역의 네 좌표값으로 영역 내 거점을 조회합니다.<br>영역의 좌표값은 위도와 경도로 구성됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetSpotResponses.class))),
            @ApiResponse(responseCode = "400", description = "거점 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "SPOT_002", value = """
                            {
                                "code": "SPOT_002",
                                "message": "거점 좌표가 유효하지 않습니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "좌표값이 null입니다."),
                    @ExampleObject(name = "SPOT_003", value = """
                            {
                                "code": "SPOT_003",
                                "message": "영역이 잘못되었습니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "영역 생성에 실패 했습니다."),
            })),
    })
    public ResponseEntity<GetSpotResponses> getAreaSpots(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = GetAreaSpotRequest.class)))
            @RequestBody GetAreaSpotRequest request
    ) {
        Polygon polygon = null;
        try {
            GeometryFactory geometryFactory = new GeometryFactory();
            List<Coordinate> coordinatesPolygon = List.of(
                    new Coordinate(request.getTopLeftPoint().getX(), request.getTopLeftPoint().getY()),
                    new Coordinate(request.getBottomLeftPoint().getX(), request.getBottomLeftPoint().getY()),
                    new Coordinate(request.getBottomRightPoint().getX(), request.getBottomRightPoint().getY()),
                    new Coordinate(request.getTopRightPoint().getX(), request.getTopRightPoint().getY()),
                    new Coordinate(request.getTopLeftPoint().getX(), request.getTopLeftPoint().getY())
            );
            polygon = geometryFactory.createPolygon(coordinatesPolygon.toArray(new Coordinate[0]));
        } catch (NullPointerException e) {
            throw new BaseException(SpotError.SPOT_COORDINATE_INVALID);
        } catch (IllegalArgumentException e) {
            throw new BaseException(SpotError.SPOT_POLYGON_INVALID);
        }
        return ResponseEntity.ok(getSpotService.getAreaSpots(polygon));
    }

    @PostMapping("/map")
    @Operation(
            summary = "원형 영역 내 거점 조회",
            description = "조회 위치 요청 반경의 원형 내 거점을 조회합니다.<br>조회하려는 구역의 반경 크기와 조회하려는 위치(x:경도, y:위도)를 입력해주세요.<br>거점 id와 위치(x:경도, y:위도)를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchWithRadiusResponses.class))),
    })
    public ResponseEntity<SearchWithRadiusResponses> getRadiusSpots(
            @Parameter(description = "거리 반경<br>단위: 미터<br>기본값: 500m")
            @RequestParam(value = "radius", defaultValue = "500", required = false) Long radius,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = SearchSpotPointRequest.class)))
            @RequestBody SearchSpotPointRequest request
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(request.getSearchPoint().getX(), request.getSearchPoint().getY());
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(getSpotService.getRadiusSpots(point, radius));
    }
}
