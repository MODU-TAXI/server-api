package com.modutaxi.api.domain.spot.controller;


import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.spot.dto.SpotRequestDto.CreateSpotRequest;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.CreateSpotResponse;
import com.modutaxi.api.domain.spot.service.RegisterSpotService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
@Tag(name = "거점 등록", description = "거점 등록 API")
public class RegisterSpotController {
    private final RegisterSpotService registerSpotService;

    @PostMapping("")
    @Operation(
            summary = "거점 등록",
            description = "거점을 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateSpotResponse.class))),
            @ApiResponse(responseCode = "400", description = "거점 등록 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "SPOT_002", value = """
                            {
                                "code": "SPOT_002",
                                "message": "거점 좌표가 유효하지 않습니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "거점 좌표가 유효하지 않습니다."),
                    @ExampleObject(name = "SPOT_004", value = """
                            {
                                "code": "SPOT_004",
                                "message": "사용중인 거점 이름입니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "사용중인 거점 이름입니다."),
            })),
    })
    public ResponseEntity<CreateSpotResponse> registerSpot(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CreateSpotRequest.class)))
            @RequestBody CreateSpotRequest request
    ) {
        if (request.getSpotPoint().getX() < -180 || request.getSpotPoint().getX() > 180 || request.getSpotPoint().getY() < -90 || request.getSpotPoint().getY() > 90) {
            throw new BaseException(SpotError.SPOT_COORDINATE_INVALID);
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(request.getSpotPoint().getX(), request.getSpotPoint().getY());
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(new CreateSpotResponse(registerSpotService.registerDestination(request.getName(), request.getAddress(), point)));
    }
}
