package com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.LegacyNoticeEndpoints.NOTICE;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.mapper.LegacyNoticeCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.productnotice.port.in.command.LegacyProductUpdateNoticeUseCase;
import com.ryuqq.marketplace.application.legacy.productnotice.port.in.query.LegacyResolveNoticeFieldsUseCase;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 고시정보 수정 API 컨트롤러.
 *
 * <p>씬 컨트롤러 원칙의 예외 — 레거시 카테고리 해석 + 필드 매핑을 컨트롤러에서 조합합니다. 레거시 flat 필드를 표준 커맨드로 변환한 뒤, 레거시 UseCase로
 * luxurydb에 저장합니다.
 */
@Tag(name = "세토프 어드민용 레거시 - 고시정보", description = "세토프 어드민용 레거시 고시정보 엔드포인트.")
@RestController
public class LegacyNoticeCommandController {

    private final LegacyResolveNoticeFieldsUseCase legacyResolveNoticeFieldsUseCase;
    private final LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase;
    private final LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper;

    public LegacyNoticeCommandController(
            LegacyResolveNoticeFieldsUseCase legacyResolveNoticeFieldsUseCase,
            LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase,
            LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper) {
        this.legacyResolveNoticeFieldsUseCase = legacyResolveNoticeFieldsUseCase;
        this.legacyProductUpdateNoticeUseCase = legacyProductUpdateNoticeUseCase;
        this.legacyNoticeCommandApiMapper = legacyNoticeCommandApiMapper;
    }

    @Operation(summary = "레거시 고시정보 수정", description = "세토프 어드민용 레거시 상품그룹의 고시정보를 수정합니다.")
    @PreAuthorize("@legacyAccess.isProductOwnerOrMaster(#productGroupId)")
    @PutMapping(NOTICE)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductNotice(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyCreateProductNoticeRequest request) {

        NoticeCategory noticeCategory = legacyResolveNoticeFieldsUseCase.execute(productGroupId);

        UpdateProductNoticeCommand command =
                legacyNoticeCommandApiMapper.toUpdateNoticeCommand(
                        productGroupId, request, noticeCategory);

        legacyProductUpdateNoticeUseCase.execute(command, noticeCategory);

        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }
}
