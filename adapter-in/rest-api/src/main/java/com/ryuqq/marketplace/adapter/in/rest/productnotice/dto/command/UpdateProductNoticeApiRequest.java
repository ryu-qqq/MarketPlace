package com.ryuqq.marketplace.adapter.in.rest.productnotice.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * UpdateProductNoticeApiRequest - 상품 그룹 고시정보 수정 API Request.
 *
 * <p>API-REQ-001: Record 패턴 사용
 *
 * <p>API-VAL-001: jakarta.validation 사용
 */
public record UpdateProductNoticeApiRequest(
        @NotNull(message = "고시 카테고리 ID는 필수입니다") Long noticeCategoryId,
        @NotNull(message = "고시 항목 목록은 필수입니다") @Valid List<NoticeEntryRequest> entries) {

    public record NoticeEntryRequest(
            @NotNull(message = "고시 필드 ID는 필수입니다") Long noticeFieldId,
            @NotBlank(message = "고시 필드 값은 필수입니다") String fieldValue) {}
}
