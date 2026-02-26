package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 인바운드 상품 이미지 수정 API Request. */
@Schema(description = "인바운드 상품 이미지 수정 요청")
public record UpdateInboundProductImagesApiRequest(
        @Schema(description = "이미지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "이미지 목록은 필수입니다")
                @Valid
                List<ImageEntry> images) {

    @Schema(description = "이미지 데이터")
    public record ImageEntry(
            @Schema(
                            description = "이미지 유형 (THUMBNAIL, DETAIL)",
                            example = "THUMBNAIL",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "이미지 타입은 필수입니다")
                    String imageType,
            @Schema(
                            description = "원본 이미지 URL",
                            example = "https://example.com/image.jpg",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @NotBlank(message = "원본 URL은 필수입니다")
                    String originUrl,
            @Schema(
                            description = "정렬 순서",
                            example = "0",
                            requiredMode = Schema.RequiredMode.REQUIRED)
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    int sortOrder) {}
}
