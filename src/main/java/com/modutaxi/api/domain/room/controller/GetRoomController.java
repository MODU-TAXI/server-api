package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.SearchRoomPointRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.SearchWithRadiusResponses;
import com.modutaxi.api.domain.room.service.GetRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
@Tag(name = "방 조회", description = "방 조회 API")
public class GetRoomController {

    /**
     * [GET] 경로를 포함한 방 정보 상세 조회
     */
    private final GetRoomService getRoomService;

    @Operation(summary = "경로를 포함한 방 정보 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailResponse> getRoomDetail(@PathVariable Long id) {
        return ResponseEntity.ok(getRoomService.getRoomDetail(id));
    }

    @Operation(summary = "경로를 제외한 방 리스트 조회",
        description = "\n\n\n**page(0 ~ )** : 페이지 번호\n\n**size(1 ~ )** : 사이즈")
    @GetMapping
    public ResponseEntity<PageResponseDto<List<RoomSimpleResponse>>> getRoomSimpleList(
        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(getRoomService.getRoomSimpleList(page, size));
    }

    @PostMapping("/map")
    @Operation(
            summary = "원형 영역 내 방 조회",
            description = "조회 위치 요청 반경의 원형 내 방을 조회합니다.<br>조회하려는 구역의 반경 크기와 조회하려는 위치(x:경도, y:위도)를 입력해주세요.<br>방 id와 위치(x:경도, y:위도), 거점명을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchWithRadiusResponses.class))),
    })
    public ResponseEntity<SearchWithRadiusResponses> getRadiusRooms(
            @Parameter(description = "거리 반경<br>단위: 미터<br>기본값: 500m")
            @RequestParam(value = "radius", defaultValue = "500", required = false) Long radius,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = SearchRoomPointRequest.class)))
            @RequestBody SearchRoomPointRequest request
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(request.getSearchPoint().getX(), request.getSearchPoint().getY());
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(getRoomService.getRadiusRooms(point, radius));
    }
}