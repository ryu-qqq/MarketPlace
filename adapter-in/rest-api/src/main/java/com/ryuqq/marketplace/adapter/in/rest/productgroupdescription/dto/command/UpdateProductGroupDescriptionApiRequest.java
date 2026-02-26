package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * UpdateProductGroupDescriptionApiRequest - 상품 그룹 상세 설명 수정 API Request.
 *
 * <p>API-REQ-001: Record 패턴 사용
 *
 * <p>API-VAL-001: jakarta.validation 사용
 */
@Schema(description = "상품 그룹 상세 설명 수정 요청")
public record UpdateProductGroupDescriptionApiRequest(
        @Schema(
                        description = "상세 설명 내용 (HTML)",
                        example = "<p>상품 상세 설명입니다.</p>",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "상세 설명 내용은 필수입니다")
                String content) {}
