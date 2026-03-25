package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** OMS 상품 상세 응답 (API 2). */
@Schema(description = "OMS 상품 상세 응답")
public record OmsProductDetailApiResponse(
        @Schema(description = "상품그룹 정보") ProductGroupResponse productGroup,
        @Schema(description = "상품(SKU) 목록") List<ProductResponse> products,
        @Schema(description = "연동 통계") SyncSummaryApiResponse syncSummary) {

    @Schema(description = "상품그룹 정보")
    public record ProductGroupResponse(
            @Schema(description = "상품그룹 ID", example = "125694305") long productGroupId,
            @Schema(description = "상품그룹명", example = "나이키 에어포스 1 '07 화이트") String productGroupName,
            @Schema(description = "셀러 ID", example = "1001") long sellerId,
            @Schema(description = "셀러명", example = "나이키코리아") String sellerName,
            @Schema(description = "카테고리 ID", example = "200101001") long categoryId,
            @Schema(description = "옵션 타입", example = "OPTION_ONE") String optionType,
            @Schema(description = "재고 관리 타입", example = "STOCK") String managementType,
            @Schema(description = "브랜드 정보") BrandResponse brand,
            @Schema(description = "가격 정보") PriceResponse price,
            @Schema(description = "대표 이미지 URL") String productGroupMainImageUrl,
            @Schema(description = "카테고리 전체명", example = "여성패션 > 아우터 > 패딩 > 롱패딩")
                    String categoryFullName,
            @Schema(description = "상품 상태") ProductStatusResponse productStatus,
            @Schema(description = "등록일", example = "2025-12-15 10:30:00") String insertDate,
            @Schema(description = "수정일", example = "2025-12-16 14:30:00") String updateDate,
            @Schema(description = "등록자") String insertOperator,
            @Schema(description = "수정자") String updateOperator) {}

    @Schema(description = "브랜드 정보")
    public record BrandResponse(
            @Schema(description = "브랜드 ID", example = "501") long brandId,
            @Schema(description = "브랜드명", example = "Nike") String brandName,
            @Schema(description = "브랜드 한글명", example = "나이키") String brandNameKo) {}

    @Schema(description = "가격 정보")
    public record PriceResponse(
            @Schema(description = "정가", example = "159000") int regularPrice,
            @Schema(description = "현재가", example = "129000") int currentPrice,
            @Schema(description = "판매가", example = "129000") int salePrice,
            @Schema(description = "즉시할인 금액", example = "0") int directDiscountPrice,
            @Schema(description = "즉시할인율", example = "0") int directDiscountRate,
            @Schema(description = "할인율", example = "19") int discountRate) {}

    @Schema(description = "상품 상태")
    public record ProductStatusResponse(
            @Schema(description = "품절 여부", example = "N") String soldOutYn,
            @Schema(description = "노출 여부", example = "Y") String displayYn) {}

    @Schema(description = "상품(SKU) 정보")
    public record ProductResponse(
            @Schema(description = "상품 ID", example = "1001") long productId,
            @Schema(description = "재고 수량", example = "10") int stockQuantity,
            @Schema(description = "상품 상태") ProductStatusResponse productStatus,
            @Schema(description = "옵션 요약", example = "250 / 화이트") String option,
            @Schema(description = "옵션 목록") List<OptionResponse> options,
            @Schema(description = "추가 금액", example = "0") int additionalPrice) {}

    @Schema(description = "옵션 정보")
    public record OptionResponse(
            @Schema(description = "옵션 그룹 ID", example = "1") long optionGroupId,
            @Schema(description = "옵션 상세 ID", example = "101") long optionDetailId,
            @Schema(description = "옵션명", example = "SIZE") String optionName,
            @Schema(description = "옵션값", example = "250") String optionValue) {}

    @Schema(description = "연동 통계")
    public record SyncSummaryApiResponse(
            @Schema(description = "전체 연동 횟수", example = "5") long totalSyncCount,
            @Schema(description = "성공 횟수", example = "3") long successCount,
            @Schema(description = "실패 횟수", example = "1") long failCount,
            @Schema(description = "대기 횟수", example = "1") long pendingCount,
            @Schema(description = "마지막 연동일", example = "2025-12-16 14:30:00")
                    String lastSyncAt) {}
}
