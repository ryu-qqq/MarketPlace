package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 상품 그룹 엑셀 배치 등록 API 요청 항목.
 *
 * <p>엑셀 업로드 기반 배치 등록 전용 DTO이며, sellerId/배송정책/환불정책은 서버에서 컨텍스트 기반으로 해석합니다.
 */
@Schema(description = "상품 그룹 엑셀 배치 등록 항목")
public record RegisterProductGroupExcelApiRequest(
        @Schema(description = "브랜드 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "브랜드 ID는 필수입니다")
                @Min(value = 1, message = "브랜드 ID는 1 이상이어야 합니다")
                Long brandId,
        @Schema(
                        description = "카테고리 ID",
                        example = "100",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "카테고리 ID는 필수입니다")
                @Min(value = 1, message = "카테고리 ID는 1 이상이어야 합니다")
                Long categoryId,
        @Schema(
                        description = "상품 그룹명",
                        example = "나이키 에어맥스 90",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "상품 그룹명은 필수입니다")
                @Size(max = 200, message = "상품 그룹명은 200자 이하여야 합니다")
                String productGroupName,
        @Schema(
                        description = "옵션 타입 (COMBINATION, SINGLE, NONE)",
                        example = "COMBINATION",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "옵션 타입은 필수입니다")
                String optionType,
        @Schema(description = "이미지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "이미지는 최소 1개 이상 필요합니다")
                @Valid
                List<RegisterProductGroupApiRequest.ImageApiRequest> images,
        @Schema(description = "옵션 그룹 목록") @Valid
                List<RegisterProductGroupApiRequest.OptionGroupApiRequest> optionGroups,
        @Schema(description = "상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "상품은 최소 1개 이상 필요합니다")
                @Valid
                List<RegisterProductGroupApiRequest.ProductApiRequest> products,
        @Schema(description = "상세 설명", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "상세 설명은 필수입니다")
                @Valid
                RegisterProductGroupApiRequest.DescriptionApiRequest description,
        @Schema(description = "고시정보", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "고시정보는 필수입니다")
                @Valid
                RegisterProductGroupApiRequest.NoticeApiRequest notice) {}
