package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * UpdateProductGroupImagesApiRequest - 상품 그룹 이미지 수정 API Request.
 *
 * <p>API-REQ-001: Record 패턴 사용
 *
 * <p>API-VAL-001: jakarta.validation 사용
 */
public record UpdateProductGroupImagesApiRequest(
        @NotNull(message = "이미지 목록은 필수입니다") @Valid List<ImageRequest> images) {

    public record ImageRequest(
            @NotBlank(message = "이미지 타입은 필수입니다") String imageType,
            @NotBlank(message = "원본 URL은 필수입니다") String originUrl,
            int sortOrder) {}
}
