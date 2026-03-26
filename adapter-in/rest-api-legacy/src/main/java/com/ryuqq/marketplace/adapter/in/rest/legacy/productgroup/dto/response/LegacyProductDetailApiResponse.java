package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 세토프 레거시 상품 상세 조회 응답 DTO.
 *
 * <p>세토프 OMS의 ProductGroupFetchResponse와 동일한 JSON 구조를 유지합니다.
 *
 * <p>상속 구조: ProductGroupFetchResponse extends ProductGroupDetailResponse → record 기반 flat 구조로 변환.
 */
public record LegacyProductDetailApiResponse(
        LegacyProductGroupInfoResponse productGroup,
        Set<LegacyProductFetchResponse> products,
        LegacyProductNoticeResponse productNotices,
        List<LegacyProductImageResponse> productGroupImages,
        String detailDescription,
        List<LegacyCategoryResponse> categories) {

    // ===== ProductGroupInfo =====

    /** 세토프 ProductGroupInfo 호환 응답. */
    public record LegacyProductGroupInfoResponse(
            long productGroupId,
            String productGroupName,
            long sellerId,
            String sellerName,
            long categoryId,
            String optionType,
            String managementType,
            LegacyBrandResponse brand,
            LegacyPriceResponse price,
            LegacyClothesDetailResponse clothesDetailInfo,
            LegacyDeliveryNoticeResponse deliveryNotice,
            LegacyRefundNoticeResponse refundNotice,
            String productGroupMainImageUrl,
            @JsonInclude(JsonInclude.Include.NON_NULL) String categoryFullName,
            LegacyProductStatusResponse productStatus,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime insertDate,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updateDate,
            String insertOperator,
            String updateOperator,
            LegacyCrawlProductInfoResponse crawlProductInfo,
            long crawlProductSku,
            List<Object> externalProductInfos,
            String externalProductUuId) {}

    /** 세토프 CrawlProductInfo 호환 응답 (기본값). */
    public record LegacyCrawlProductInfoResponse(
            String siteName,
            long crawlProductSku,
            String baseLinkUrl,
            String status,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime insertDate,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updateDate) {

        public static LegacyCrawlProductInfoResponse defaultValue() {
            LocalDateTime now = LocalDateTime.now();
            return new LegacyCrawlProductInfoResponse("", 0, "", "PENDING", now, now);
        }
    }

    // ===== Embedded Value Objects =====

    /** 세토프 BaseBrandContext 호환 응답. */
    public record LegacyBrandResponse(long brandId, String brandName) {}

    /** 세토프 Price embedded 호환 응답. */
    public record LegacyPriceResponse(
            BigDecimal regularPrice,
            BigDecimal currentPrice,
            BigDecimal salePrice,
            BigDecimal directDiscountPrice,
            int directDiscountRate,
            int discountRate) {}

    /** 세토프 ClothesDetail embedded 호환 응답. */
    public record LegacyClothesDetailResponse(
            String productCondition, String origin, String styleCode) {}

    /** 세토프 DeliveryNotice embedded 호환 응답. */
    public record LegacyDeliveryNoticeResponse(
            String deliveryArea, long deliveryFee, int deliveryPeriodAverage) {}

    /** 세토프 RefundNotice embedded 호환 응답. */
    public record LegacyRefundNoticeResponse(
            String returnMethodDomestic,
            String returnCourierDomestic,
            int returnChargeDomestic,
            String returnExchangeAreaDomestic) {}

    // ===== Notice / Image =====

    /** 세토프 ProductNoticeDto 호환 응답. */
    public record LegacyProductNoticeResponse(
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonth,
            String assuranceStandard,
            String asPhone) {}

    /** 세토프 ProductImageDto 호환 응답. */
    public record LegacyProductImageResponse(String type, String productImageUrl) {}

    // ===== Category =====

    /** 세토프 TreeCategoryContext 호환 응답. */
    public record LegacyCategoryResponse(
            long categoryId,
            String categoryName,
            String displayName,
            int categoryDepth,
            long parentCategoryId,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) List<LegacyCategoryResponse> children) {}
}
