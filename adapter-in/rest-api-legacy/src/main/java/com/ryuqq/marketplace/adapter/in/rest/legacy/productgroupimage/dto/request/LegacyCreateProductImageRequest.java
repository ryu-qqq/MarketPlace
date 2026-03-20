package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/** 세토프 CreateProductImage 호환 요청 DTO. */
public record LegacyCreateProductImageRequest(
        @NotBlank(message = "이미지 타입(type)은 필수입니다.") String type,
        @NotBlank(message = "상품 이미지 URL은 필수입니다.")
                @Length(max = 1000, message = "상품 이미지 URL은 1000자를 초과할 수 없습니다.")
                String productImageUrl,
        @NotBlank(message = "원본 이미지 URL은 필수입니다.")
                @Length(max = 1000, message = "원본 이미지 URL은 1000자를 초과할 수 없습니다.")
                String originUrl) {}
