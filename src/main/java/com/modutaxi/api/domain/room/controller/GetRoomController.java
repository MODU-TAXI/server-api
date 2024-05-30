package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.SearchRoomWithRadiusResponses;
import com.modutaxi.api.domain.room.entity.RoomSortType;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.modutaxi.api.domain.room.service.GetRoomService;
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
    public ResponseEntity<RoomDetailResponse> getRoomDetail(@CurrentMember Member member, @PathVariable Long id) {
        return ResponseEntity.ok(getRoomService.getRoomDetail(member, id));
    }

    @Operation(summary = "경로를 제외한 방 리스트 조회",
        description = "조회 위치 요청 반경의 원형 내 방의 정보를 리스트로 조회합니다.<br>**필수 파라미터**<br>&emsp;**page**(페이지 번호)<br>&emsp;**size**(사이즈)<br>&emsp;**searchLongitude**(경도)<br>&emsp;**searchLatitude**(위도)<br><br>**선택 파라미터**<br>&emsp;**spotId**(지정 거점id)<br>&emsp;**radius**(조회 반경) : 기본값(500m)<br>&emsp;**roomTags**(방 카테고리)<br>&emsp;**isImminent**(마감 임박 여부)<br>&emsp;**sortType**(정렬 기준)<br><br>**page(0 ~ )** : 페이지 번호<br>**size(1 ~ )** : 사이즈<br>**sortType**<br>&emsp;**NEW** : 1. 가장 최근에 생성된 순서로 정렬 2. 가장 가까운 순서로 정렬<br>&emsp;**DISTANCE** : 1. 가장 가까운 순서로 정렬 2. 출발시간이 가장 이른 순서로 정렬<br>&emsp;**ENDTIME** : 1. 출발시간이 가장 이른 순서로 정렬 2. 가장 가까운 순서로 정렬<br><br>응답값은 **PageResponseDto** 내에 result로 **RoomSimpleResponse** 가 들어간 형식으로 반환됩니다."
    )
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
        @RequestParam int page,
        @RequestParam int size,
        @Parameter(description = "거점 id") @RequestParam(required = false) Long spotId,
        @Parameter(description = "거리 반경<br>단위: 미터<br>기본값: 500m") @RequestParam(defaultValue = "500", required = false) Long radius,
        @Parameter(description = "방 카테고리") @RequestParam(required = false) List<RoomTagBitMask> roomTags,
        @Parameter(description = "경도") @RequestParam(value = "searchLongitude") Float searchLongitude,
        @Parameter(description = "위도") @RequestParam(value = "searchLatitude") Float searchLatitude,
        @Parameter(description = "마감임박 여부") @RequestParam(defaultValue = "false") Boolean isImminent,
        @Parameter(description = "정렬타입") @RequestParam RoomSortType sortType
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(searchLongitude, searchLatitude);
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(getRoomService.getRoomSimpleList(page, size, spotId, roomTags, point, radius, isImminent, sortType));
    }

    @GetMapping("/map")
    @Operation(
        summary = "원형 영역 내 방 조회",
        description = "조회 위치 요청 반경의 원형 내 방의 좌표를 모두 조회합니다.<br>**필수 파라미터**<br>&emsp;**searchLongitude**(경도)<br>&emsp;**searchLatitude**(위도)<br><br>**선택 파라미터**<br>&emsp;**spotId**(지정 거점id)<br>&emsp;**radius**(조회 반경) : 기본값(500m)<br>&emsp;**roomTags**(방 카테고리)<br>&emsp;**isImminent**(마감 임박 여부)<br><br>**방 id**와 **longitude**(경도), **latitude**(위도), **거점명**을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchRoomWithRadiusResponses.class))),
    })
    public ResponseEntity<SearchRoomWithRadiusResponses> getRadiusRooms(
        @Parameter(description = "거점 id") @RequestParam(required = false) Long spotId,
        @Parameter(description = "거리 반경<br>단위: 미터<br>기본값: 500m") @RequestParam(defaultValue = "500", required = false) Long radius,
        @Parameter(description = "방 카테고리") @RequestParam(required = false) List<RoomTagBitMask> roomTags,
        @Parameter(description = "경도") @RequestParam(value = "searchLongitude") Float searchLongitude,
        @Parameter(description = "위도") @RequestParam(value = "searchLatitude") Float searchLatitude,
        @Parameter(description = "마감임박 여부") @RequestParam(defaultValue = "false") Boolean isImminent
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(searchLongitude, searchLatitude);
        Point point = geometryFactory.createPoint(coordinate);
        return ResponseEntity.ok(getRoomService.getRadiusRooms(spotId, roomTags, point, radius, isImminent));
    }
}