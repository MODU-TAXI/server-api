package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.service.GetRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}