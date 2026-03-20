package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.LegacyProductGroupImageEndpoints.IMAGES;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper.LegacyImageCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.image.port.in.command.LegacyProductUpdateImagesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 어드민용 레거시 상품그룹 이미지 수정 API 컨트롤러. */
@Tag(name = "세토프 어드민용 레거시 - 이미지", description = "세토프 어드민용 레거시 상품그룹 이미지 엔드포인트.")
@RestController
public class LegacyProductGroupImageCommandController {

    private final LegacyProductUpdateImagesUseCase legacyProductUpdateImagesUseCase;
    private final LegacyImageCommandApiMapper legacyImageCommandApiMapper;

    public LegacyProductGroupImageCommandController(
            LegacyProductUpdateImagesUseCase legacyProductUpdateImagesUseCase,
            LegacyImageCommandApiMapper legacyImageCommandApiMapper) {
        this.legacyProductUpdateImagesUseCase = legacyProductUpdateImagesUseCase;
        this.legacyImageCommandApiMapper = legacyImageCommandApiMapper;
    }

    @Operation(summary = "레거시 상품그룹 이미지 수정", description = "세토프 어드민용 레거시 상품그룹의 이미지 목록을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping(IMAGES)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductImages(
            @PathVariable long productGroupId,
            @Valid @RequestBody List<LegacyCreateProductImageRequest> request) {
        legacyProductUpdateImagesUseCase.execute(
                legacyImageCommandApiMapper.toLegacyUpdateImagesCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }
}
