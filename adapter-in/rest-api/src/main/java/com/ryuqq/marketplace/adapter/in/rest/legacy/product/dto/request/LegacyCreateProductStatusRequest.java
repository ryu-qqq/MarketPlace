package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** 세토프 CreateProductStatus 호환 요청 DTO. */
public record LegacyCreateProductStatusRequest(
        @NotBlank(message = "품절 여부는 필수입니다.")
                @Pattern(regexp = "Y|N", message = "품절 여부는 Y 또는 N 이어야 합니다.")
                String soldOutYn,
        @NotBlank(message = "진열 여부는 필수입니다.")
                @Pattern(regexp = "Y|N", message = "진열 여부는 Y 또는 N 이어야 합니다.")
                String displayYn) {}
