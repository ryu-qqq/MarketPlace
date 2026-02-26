package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 인바운드 상품 수신 요청 DTO (크롤링 등 외부 소스용).
 *
 * <p>내부 상품 등록({@code RegisterProductGroupApiRequest})과 동일한 구조를 따르되, 인바운드 고유 필드(inboundSourceId,
 * externalProductCode 등)를 추가합니다.
 */
@Schema(description = "인바운드 상품 수신 요청")
public record ReceiveInboundProductApiRequest(
        @Schema(description = "인바운드 소스 ID", example = "1")
                @Min(value = 1, message = "인바운드 소스 ID는 1 이상이어야 합니다")
                long inboundSourceId,
        @Schema(description = "외부 상품 코드", example = "EXT-001")
                @NotBlank(message = "외부 상품 코드는 필수입니다")
                String externalProductCode,
        @Schema(description = "상품명", example = "나이키 에어맥스 90")
                @NotBlank(message = "상품명은 필수입니다")
                @Size(max = 500, message = "상품명은 500자 이하여야 합니다")
                String productName,
        @Schema(description = "외부 브랜드 코드", example = "BRAND-001")
                @NotBlank(message = "외부 브랜드 코드는 필수입니다")
                String externalBrandCode,
        @Schema(description = "외부 카테고리 코드", example = "CAT-001")
                @NotBlank(message = "외부 카테고리 코드는 필수입니다")
                String externalCategoryCode,
        @Schema(description = "셀러 ID", example = "1")
                @Min(value = 1, message = "셀러 ID는 1 이상이어야 합니다")
                long sellerId,
        @Schema(description = "대표 정가", example = "30000")
                @Min(value = 0, message = "정가는 0 이상이어야 합니다")
                int regularPrice,
        @Schema(description = "대표 현재가", example = "25000")
                @Min(value = 0, message = "현재가는 0 이상이어야 합니다")
                int currentPrice,
        @Schema(description = "옵션 타입 (COMBINATION, SINGLE, NONE)", example = "SINGLE")
                @NotBlank(message = "옵션 타입은 필수입니다")
                String optionType,
        @Schema(description = "이미지 목록") @NotEmpty(message = "이미지는 최소 1개 이상 필요합니다") @Valid
                List<ImageRequest> images,
        @Schema(description = "옵션 그룹 목록") @Valid List<OptionGroupRequest> optionGroups,
        @Schema(description = "상품 목록") @NotEmpty(message = "상품은 최소 1개 이상 필요합니다") @Valid
                List<ProductRequest> products,
        @Schema(description = "상세 설명") @NotNull(message = "상세 설명은 필수입니다") @Valid
                DescriptionRequest description,
        @Schema(description = "고시정보") @Valid NoticeRequest notice) {

    public ReceiveInboundProductApiRequest {
        images = images != null ? List.copyOf(images) : List.of();
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
        products = products != null ? List.copyOf(products) : List.of();
    }

    @Schema(description = "이미지 데이터")
    public record ImageRequest(
            @Schema(description = "이미지 유형 (THUMBNAIL, DETAIL)", example = "THUMBNAIL")
                    @NotBlank(message = "이미지 타입은 필수입니다")
                    String imageType,
            @Schema(description = "원본 이미지 URL", example = "https://example.com/image.jpg")
                    @NotBlank(message = "이미지 URL은 필수입니다")
                    String originUrl,
            @Schema(description = "정렬 순서", example = "0")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    int sortOrder) {}

    @Schema(description = "옵션 그룹 데이터")
    public record OptionGroupRequest(
            @Schema(description = "옵션 그룹명", example = "색상") @NotBlank(message = "옵션 그룹명은 필수입니다")
                    String optionGroupName,
            @Schema(description = "입력 유형 (PREDEFINED, FREE_INPUT)", example = "PREDEFINED")
                    String inputType,
            @Schema(description = "옵션 값 목록") @NotEmpty(message = "옵션 값은 최소 1개 이상 필요합니다") @Valid
                    List<OptionValueRequest> optionValues) {

        public OptionGroupRequest {
            optionValues = optionValues != null ? List.copyOf(optionValues) : List.of();
        }
    }

    @Schema(description = "옵션 값 데이터")
    public record OptionValueRequest(
            @Schema(description = "옵션 값명", example = "블랙") @NotBlank(message = "옵션 값명은 필수입니다")
                    String optionValueName,
            @Schema(description = "정렬 순서", example = "0")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    int sortOrder) {}

    @Schema(description = "상품 데이터")
    public record ProductRequest(
            @Schema(description = "SKU 코드", example = "SKU-001") String skuCode,
            @Schema(description = "정가", example = "30000")
                    @Min(value = 0, message = "정가는 0 이상이어야 합니다")
                    int regularPrice,
            @Schema(description = "현재가", example = "25000")
                    @Min(value = 0, message = "현재가는 0 이상이어야 합니다")
                    int currentPrice,
            @Schema(description = "재고 수량", example = "100")
                    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                    int stockQuantity,
            @Schema(description = "정렬 순서", example = "0")
                    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
                    int sortOrder,
            @Schema(description = "선택된 옵션 목록") @Valid List<SelectedOptionRequest> selectedOptions) {

        public ProductRequest {
            selectedOptions = selectedOptions != null ? List.copyOf(selectedOptions) : List.of();
        }
    }

    @Schema(description = "선택된 옵션 데이터")
    public record SelectedOptionRequest(
            @Schema(description = "옵션 그룹명", example = "색상") @NotBlank(message = "옵션 그룹명은 필수입니다")
                    String optionGroupName,
            @Schema(description = "옵션 값명", example = "블랙") @NotBlank(message = "옵션 값명은 필수입니다")
                    String optionValueName) {}

    @Schema(description = "상세 설명 데이터")
    public record DescriptionRequest(
            @Schema(description = "상세 설명 내용 (HTML)", example = "<p>상품 상세 설명입니다.</p>")
                    @NotBlank(message = "상세설명 내용은 필수입니다")
                    String content) {}

    @Schema(description = "고시정보 데이터")
    public record NoticeRequest(
            @Schema(description = "고시 항목 목록") @Valid List<NoticeEntryRequest> entries) {

        public NoticeRequest {
            entries = entries != null ? List.copyOf(entries) : List.of();
        }
    }

    @Schema(description = "고시정보 항목 데이터")
    public record NoticeEntryRequest(
            @Schema(description = "고시정보 필드 코드", example = "MATERIAL")
                    @NotBlank(message = "고시정보 필드 코드는 필수입니다")
                    String fieldCode,
            @Schema(description = "고시정보 필드 값", example = "면 100%")
                    @NotBlank(message = "고시정보 필드 값은 필수입니다")
                    String fieldValue) {}
}
