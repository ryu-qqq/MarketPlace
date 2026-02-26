package com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 수동 이미지 변환 요청 API DTO.
 *
 * @param variantTypes 변환 대상 Variant 타입 목록 (null이면 전체 타입 대상)
 */
@Schema(description = "수동 이미지 변환 요청")
public record RequestImageTransformApiRequest(
        @Schema(
                        description = "변환 대상 Variant 타입 목록 (비어있으면 전체 타입 대상)",
                        example = "[\"SMALL_WEBP\", \"MEDIUM_WEBP\"]")
                List<String> variantTypes) {}
