package com.ryuqq.marketplace.adapter.in.rest.legacy.product.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUP_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductQueryApiMapper;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 상품 조회 API 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다.
 *
 * <p>API-CTR-010: CQRS Controller 분리 (Query 전용).
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyProductQueryController {

    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyProductQueryApiMapper legacyProductQueryApiMapper;

    public LegacyProductQueryController(
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyProductQueryApiMapper legacyProductQueryApiMapper) {
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.legacyProductQueryApiMapper = legacyProductQueryApiMapper;
    }

    @GetMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<LegacyApiResponse<LegacyProductDetailApiResponse>> fetchProductGroup(
            @PathVariable long productGroupId) {
        LegacyProductGroupDetailResult result = legacyProductQueryUseCase.execute(productGroupId);
        LegacyProductDetailApiResponse response = legacyProductQueryApiMapper.toResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.of(response));
    }
}
