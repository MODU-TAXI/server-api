package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.SearchWithRadiusResponses;
import com.modutaxi.api.domain.room.service.GetRoomService;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
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
            description = "방 리스트를 조회합니다.<br><br>**spotId**(찾으려는 거점 id, 필수 x), **isImminent**(마감 임박 여부), **roomTags**(찾으려는 방 태그, 필수 x)를 함께 보내주세요.<br><br> **roomTag** : 'ONLY_WOMAN', 'MANNER', 'STUDENT_CERTIFICATION'<br><br>**page(0 ~ )** : 페이지 번호<br><br>**size(1 ~ )** : 사이즈")
    @GetMapping("/list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomSimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "방 목록 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "SPOT_001", value = """
                            {
                                "code": "SPOT_001",
                                "message": "존재하지 않는 거점 ID 입니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "일치하는 ID를 가진 거점이 존재하지 않습니다.")
            })),
            @ApiResponse(responseCode = "409", description = "방 목록 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "ROOM_004", value = """
                            {
                                "code": "ROOM_004",
                                "message": "성별 제한이 둘 다 설정되어 있습니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "성별 제한이 둘 다 설정되어 있습니다.")
            })),
    })
    public ResponseEntity<PageResponseDto<List<RoomSimpleResponse>>> getRoomSimpleList(
            @RequestParam int page, @RequestParam int size, @RequestParam(required = false) Long spotId, @RequestParam(required = false) List<RoomTagBitMask> roomTags, @RequestParam(required = false) Boolean isImminent) {
        return ResponseEntity.ok(getRoomService.getRoomSimpleList(page, size, spotId, roomTags, isImminent));
    }

    @GetMapping("/map")
    @Operation(
            summary = "원형 영역 내 방 조회",
            description = "조회 위치 요청 반경의 원형 내 방을 조회합니다.<br>조회하려는 구역의 반경 크기와 조회하려는 longitude(경도), latitude(위도)를 입력해주세요.<br>방 id와 longitude:경도, latitude:위도, 거점명을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchWithRadiusResponses.class))),
    })
    public ResponseEntity<SearchWithRadiusResponses> getRadiusRooms(
            @Parameter(description = "거리 반경<br>단위: 미터<br>기본값: 500m")
            @RequestParam(value = "radius", defaultValue = "500", required = false) Long radius,
            @RequestParam(value = "longitude") Float longitude,
            @RequestParam(value = "latitude") Float latitude
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(getRoomService.getRadiusRooms(point, radius));
    }
}