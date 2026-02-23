package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 인바운드 상품 상세설명 수정 API Request. */
@Schema(description = "인바운드 상품 상세설명 수정 요청")
public record UpdateInboundProductDescriptionApiRequest(
        @Schema(description = "상세 설명 HTML", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "상세 설명은 필수입니다")
                String content) {}
