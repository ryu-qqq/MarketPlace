package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * 레거시 상품그룹 목록 조회 응답 DTO.
 *
 * <p>세토프 어드민의 CustomPageable&lt;ProductGroupDetailResponse&gt; 호환 형식입니다.
 * content 내부가 productGroup + products nested 구조입니다.
 *
 * <p>API-DTO-003: Response DTO 설계 규칙.
 */
@Schema(description = "레거시 상품그룹 목록 조회 응답")
public record LegacyProductGroupListApiResponse(
        @Schema(description = "상품그룹 목록") List<LegacyProductGroupDetailItem> content,
        @Schema(description = "전체 건수") long totalElements,
        @Schema(description = "전체 페이지 수") int totalPages,
        @Schema(description = "페이지 크기") int size,
        @Schema(description = "현재 페이지 번호") int number,
        @Schema(description = "첫 페이지 여부") boolean first,
        @Schema(description = "마지막 페이지 여부") boolean last,
        @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
        @Schema(description = "이전 페이지 존재 여부") boolean hasPrevious) {

    public static LegacyProductGroupListApiResponse of(
            List<LegacyProductGroupDetailItem> content,
            long totalElements,
            int page,
            int size) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean isFirst = page == 0;
        boolean isLast = page >= totalPages - 1;
        boolean hasNextPage = page < totalPages - 1;
        boolean hasPreviousPage = page > 0;

        return new LegacyProductGroupListApiResponse(
                content, totalElements, totalPages, size, page, isFirst, isLast, hasNextPage,
                hasPreviousPage);
    }

    /** 레거시 ProductGroupDetailResponse 호환 — productGroup + products nested. */
    @Schema(description = "상품그룹 상세 아이템")
    public record LegacyProductGroupDetailItem(
            @Schema(description = "상품그룹 정보") LegacyProductGroupInfo productGroup,
            @Schema(description = "SKU 목록") List<LegacyProductItem> products) {}

    /** 레거시 ProductGroupInfo 호환. */
    @Schema(description = "상품그룹 정보")
    public record LegacyProductGroupInfo(
            @Schema(description = "상품그룹 ID") long productGroupId,
            @Schema(description = "상품그룹명") String productGroupName,
            @Schema(description = "판매자 ID") long sellerId,
            @Schema(description = "판매자명") String sellerName,
            @Schema(description = "카테고리 ID") long categoryId,
            @Schema(description = "옵션 타입") String optionType,
            @Schema(description = "관리유형") String managementType,
            @Schema(description = "브랜드") LegacyBrandInfo brand,
            @Schema(description = "가격") LegacyPriceInfo price,
            @Schema(description = "메인 이미지 URL") String productGroupMainImageUrl,
            @Schema(description = "카테고리 전체 경로") String categoryFullName,
            @Schema(description = "상품 상태") LegacyProductStatusInfo productStatus,
            @Schema(description = "등록일") java.time.LocalDateTime insertDate,
            @Schema(description = "수정일") java.time.LocalDateTime updateDate) {}

    /** 레거시 BaseBrandContext 호환. */
    @Schema(description = "브랜드 정보")
    public record LegacyBrandInfo(
            @Schema(description = "브랜드 ID") long id,
            @Schema(description = "브랜드명") String brandName) {}

    /** 레거시 Price 호환. */
    @Schema(description = "가격 정보")
    public record LegacyPriceInfo(
            @Schema(description = "정상가") BigDecimal regularPrice,
            @Schema(description = "판매가") BigDecimal salePrice,
            @Schema(description = "할인율") int discountRate) {}

    /** 레거시 ProductStatus 호환. */
    @Schema(description = "상품 상태")
    public record LegacyProductStatusInfo(
            @Schema(description = "품절 여부") String soldOutYn,
            @Schema(description = "진열 여부") String displayYn) {

        public static LegacyProductStatusInfo of(String status) {
            boolean soldOut = "SOLD_OUT".equals(status);
            boolean hidden = "HIDDEN".equals(status);
            return new LegacyProductStatusInfo(
                    soldOut ? "Y" : "N",
                    hidden ? "N" : "Y");
        }

        public static LegacyProductStatusInfo of(boolean soldOut, boolean displayed) {
            return new LegacyProductStatusInfo(
                    soldOut ? "Y" : "N",
                    displayed ? "Y" : "N");
        }
    }

    /** 레거시 ProductFetchResponse 호환. */
    @Schema(description = "SKU 정보")
    public record LegacyProductItem(
            @Schema(description = "상품 ID") long productId,
            @Schema(description = "재고") int stockQuantity,
            @Schema(description = "옵션 요약") String option) {}
}
