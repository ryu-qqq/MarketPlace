package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/** 세토프 CreateClothesDetail 호환 요청 DTO. */
public record LegacyCreateClothesDetailRequest(
        @NotBlank(message = "상품 상태는 필수입니다.") String productCondition,
        String origin,
        @Length(max = 50, message = "스타일 코드는 50자를 초과할 수 없습니다.") String styleCode) {}
