package com.modutaxi.api.domain.spot.controller;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.spot.dto.SpotRequestDto.UpdateSpotRequest;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.DeleteSpotResponse;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.UpdateSpotResponse;
import com.modutaxi.api.domain.spot.service.UpdateSpotService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
@Tag(name = "거점 수정", description = "거점 수정 API")
public class UpdateSpotController {
    private final UpdateSpotService updateSpotService;

    @DeleteMapping("/{id}")
    @Operation(
            summary = "특정 거점 제거",
            description = "특정 id의 거점을 제거합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteSpotResponse.class))),
    })
    public ResponseEntity<DeleteSpotResponse> deleteSpot(
            @Parameter(description = "거점 id")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new DeleteSpotResponse(updateSpotService.deleteSpot(id)));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "특정 거점 수정",
            description = "특정 id의 거점을 수정합니다.<br>이름, 주소, 좌표를 수정할 수 있습니다.<br>좌표를 수정하려면 위도와 경도 모두 입력해주세요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateSpotResponse.class))),
            @ApiResponse(responseCode = "400", description = "거점 등록 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "SPOT_001", value = """
                            {
                                "code": "SPOT_001",
                                "message": "존재하지 않는 거점 ID 입니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "일치하는 ID를 가진 거점이 존재하지 않습니다."),
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
    public ResponseEntity<UpdateSpotResponse> updateSpot(
            @Parameter(description = "거점 id", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = UpdateSpotRequest.class)))
            @RequestBody UpdateSpotRequest request
    ) {
        Point point = null;
        if (request.getLongitude() != null && request.getLatitude() != null) {
            if (request.getLongitude() < -180 || request.getLongitude() > 180 || request.getLatitude() < -90 || request.getLatitude() > 90) {
                throw new BaseException(SpotError.SPOT_COORDINATE_INVALID);
            }
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinate = new Coordinate(request.getLongitude(), request.getLatitude());
            point = geometryFactory.createPoint(coordinate);
        }
        return ResponseEntity.ok(new UpdateSpotResponse(updateSpotService.updateSpot(id, request.getName(), request.getAddress(), point)));
    }
}
