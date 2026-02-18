package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command;

import jakarta.validation.constraints.NotBlank;

/**
 * UpdateProductGroupDescriptionApiRequest - 상품 그룹 상세 설명 수정 API Request.
 *
 * <p>API-REQ-001: Record 패턴 사용
 *
 * <p>API-VAL-001: jakarta.validation 사용
 */
public record UpdateProductGroupDescriptionApiRequest(
        @NotBlank(message = "상세 설명 내용은 필수입니다") String content) {}
