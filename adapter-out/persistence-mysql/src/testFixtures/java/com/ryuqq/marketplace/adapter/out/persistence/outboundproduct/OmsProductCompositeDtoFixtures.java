package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * OMS 상품 Composite DTO 테스트 Fixtures.
 *
 * <p>Composition 조회 테스트용 DTO 생성.
 */
public final class OmsProductCompositeDtoFixtures {

    private OmsProductCompositeDtoFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_BRAND_ID = 10L;
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품";
    public static final String DEFAULT_STATUS_ACTIVE = "ACTIVE";
    public static final String DEFAULT_STATUS_INACTIVE = "INACTIVE";
    public static final String DEFAULT_STATUS_DELETED = "DELETED";
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final String DEFAULT_BRAND_NAME = "테스트 브랜드";
    public static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    public static final int DEFAULT_PRICE = 50000;
    public static final int DEFAULT_STOCK = 100;
    public static final String DEFAULT_SYNC_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_SYNC_STATUS_FAILED = "FAILED";
    public static final String DEFAULT_SYNC_STATUS_PENDING = "PENDING";

    // ========================================================================
    // OmsProductListCompositeDto Fixtures
    // ========================================================================

    /** 기본 ACTIVE 상태 OmsProductListCompositeDto. */
    public static OmsProductListCompositeDto activeCompositeDto() {
        return activeCompositeDto(DEFAULT_PRODUCT_GROUP_ID);
    }

    /** 지정 productGroupId의 ACTIVE 상태 OmsProductListCompositeDto. */
    public static OmsProductListCompositeDto activeCompositeDto(Long productGroupId) {
        Instant now = Instant.now();
        return new OmsProductListCompositeDto(
                productGroupId,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_STATUS_ACTIVE,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                now.minusSeconds(86400),
                now);
    }

    /** INACTIVE 상태 OmsProductListCompositeDto. */
    public static OmsProductListCompositeDto inactiveCompositeDto(Long productGroupId) {
        Instant now = Instant.now();
        return new OmsProductListCompositeDto(
                productGroupId,
                "비활성 상품",
                DEFAULT_STATUS_INACTIVE,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                now.minusSeconds(172800),
                now.minusSeconds(86400));
    }

    /** 특정 셀러 ID를 가진 OmsProductListCompositeDto. */
    public static OmsProductListCompositeDto compositeDtoWithSeller(
            Long productGroupId, Long sellerId, String sellerName) {
        Instant now = Instant.now();
        return new OmsProductListCompositeDto(
                productGroupId,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_STATUS_ACTIVE,
                sellerId,
                sellerName,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                now.minusSeconds(3600),
                now);
    }

    /** 목록 형태의 OmsProductListCompositeDto 생성. */
    public static List<OmsProductListCompositeDto> compositeDtoList(int count) {
        return java.util.stream.LongStream.rangeClosed(1, count)
                .mapToObj(i -> activeCompositeDto(DEFAULT_PRODUCT_GROUP_ID + i - 1))
                .toList();
    }

    // ========================================================================
    // OmsProductMainImageDto Fixtures
    // ========================================================================

    /** 기본 대표 이미지 DTO. */
    public static OmsProductMainImageDto mainImageDto() {
        return mainImageDto(DEFAULT_PRODUCT_GROUP_ID);
    }

    /** 지정 productGroupId의 대표 이미지 DTO. */
    public static OmsProductMainImageDto mainImageDto(Long productGroupId) {
        return new OmsProductMainImageDto(productGroupId, DEFAULT_IMAGE_URL);
    }

    /** productGroupId → OmsProductMainImageDto 맵 생성. */
    public static Map<Long, OmsProductMainImageDto> mainImageMap(List<Long> productGroupIds) {
        return productGroupIds.stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                id -> id,
                                id ->
                                        new OmsProductMainImageDto(
                                                id, "https://example.com/image-" + id + ".jpg")));
    }

    // ========================================================================
    // OmsProductPriceStockDto Fixtures
    // ========================================================================

    /** 기본 가격/재고 DTO. */
    public static OmsProductPriceStockDto priceStockDto() {
        return priceStockDto(DEFAULT_PRODUCT_GROUP_ID);
    }

    /** 지정 productGroupId의 가격/재고 DTO. */
    public static OmsProductPriceStockDto priceStockDto(Long productGroupId) {
        return new OmsProductPriceStockDto(productGroupId, DEFAULT_PRICE, DEFAULT_STOCK);
    }

    /** 재고 없는 가격/재고 DTO. */
    public static OmsProductPriceStockDto outOfStockDto(Long productGroupId) {
        return new OmsProductPriceStockDto(productGroupId, DEFAULT_PRICE, 0);
    }

    /** productGroupId → OmsProductPriceStockDto 맵 생성. */
    public static Map<Long, OmsProductPriceStockDto> priceStockMap(List<Long> productGroupIds) {
        return productGroupIds.stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                id -> id,
                                id ->
                                        new OmsProductPriceStockDto(
                                                id, DEFAULT_PRICE, DEFAULT_STOCK)));
    }

    // ========================================================================
    // OmsProductSyncInfoDto Fixtures
    // ========================================================================

    /** COMPLETED 상태의 연동정보 DTO. */
    public static OmsProductSyncInfoDto completedSyncInfoDto() {
        return completedSyncInfoDto(DEFAULT_PRODUCT_GROUP_ID);
    }

    /** 지정 productGroupId의 COMPLETED 상태 연동정보 DTO. */
    public static OmsProductSyncInfoDto completedSyncInfoDto(Long productGroupId) {
        return new OmsProductSyncInfoDto(
                productGroupId, DEFAULT_SYNC_STATUS_COMPLETED, Instant.now().minusSeconds(3600));
    }

    /** FAILED 상태의 연동정보 DTO. */
    public static OmsProductSyncInfoDto failedSyncInfoDto(Long productGroupId) {
        return new OmsProductSyncInfoDto(
                productGroupId, DEFAULT_SYNC_STATUS_FAILED, Instant.now().minusSeconds(7200));
    }

    /** PENDING 상태의 연동정보 DTO. */
    public static OmsProductSyncInfoDto pendingSyncInfoDto(Long productGroupId) {
        return new OmsProductSyncInfoDto(productGroupId, DEFAULT_SYNC_STATUS_PENDING, null);
    }

    /** productGroupId → OmsProductSyncInfoDto 맵 생성 (COMPLETED 상태). */
    public static Map<Long, OmsProductSyncInfoDto> completedSyncInfoMap(
            List<Long> productGroupIds) {
        return productGroupIds.stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                id -> id,
                                id ->
                                        new OmsProductSyncInfoDto(
                                                id,
                                                DEFAULT_SYNC_STATUS_COMPLETED,
                                                Instant.now().minusSeconds(3600))));
    }

    /** 비어있는 연동정보 맵 (미연동 상태 시뮬레이션). */
    public static Map<Long, OmsProductSyncInfoDto> emptySyncInfoMap() {
        return Map.of();
    }
}
