package com.ryuqq.marketplace.domain.inboundproduct;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;

/** InboundProduct 도메인 테스트 Fixtures. */
public final class InboundProductFixtures {

    private InboundProductFixtures() {}

    public static final Long DEFAULT_INBOUND_SOURCE_ID = 10L;
    public static final String DEFAULT_EXTERNAL_PRODUCT_CODE = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "EXT-BRAND-001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "EXT-CAT-001";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 200L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_GROUP_ID = 300L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_RESOLVED_SHIPPING_POLICY_ID = 50L;
    public static final Long DEFAULT_RESOLVED_REFUND_POLICY_ID = 60L;
    public static final Long DEFAULT_RESOLVED_NOTICE_CATEGORY_ID = 70L;
    public static final String DEFAULT_RAW_PAYLOAD =
            "{\"inboundSourceId\":10,\"externalProductCode\":\"EXT-PROD-001\","
                    + "\"productName\":\"테스트 상품\",\"externalBrandCode\":\"EXT-BRAND-001\","
                    + "\"externalCategoryCode\":\"EXT-CAT-001\",\"sellerId\":1,"
                    + "\"regularPrice\":10000,\"currentPrice\":9000,\"optionType\":\"NONE\","
                    + "\"images\":[],\"optionGroups\":[],\"products\":[],"
                    + "\"description\":null,\"notice\":null}";

    // ===== ID Fixtures =====

    public static InboundProductId defaultId() {
        return InboundProductId.of(1L);
    }

    public static InboundProductId id(Long value) {
        return InboundProductId.of(value);
    }

    // ===== Aggregate Fixtures =====

    /** 신규 수신 InboundProduct (RECEIVED 상태, 매핑 정보 없음). */
    public static InboundProduct newInboundProduct() {
        return InboundProduct.forNew(
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_SELLER_ID,
                DEFAULT_RAW_PAYLOAD,
                CommonVoFixtures.now());
    }

    /** inboundSourceId, externalProductCode를 지정한 신규 InboundProduct. */
    public static InboundProduct newInboundProduct(
            Long inboundSourceId, String externalProductCode) {
        return InboundProduct.forNew(
                inboundSourceId,
                ExternalProductCode.of(externalProductCode),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_SELLER_ID,
                DEFAULT_RAW_PAYLOAD,
                CommonVoFixtures.now());
    }

    /** RECEIVED 상태로 복원된 InboundProduct. */
    public static InboundProduct receivedProduct() {
        return InboundProduct.reconstitute(
                InboundProductId.of(1L),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                InboundProductStatus.RECEIVED,
                DEFAULT_RAW_PAYLOAD,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** ID를 지정한 RECEIVED 상태 InboundProduct. */
    public static InboundProduct receivedProduct(Long id) {
        return InboundProduct.reconstitute(
                InboundProductId.of(id),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                InboundProductStatus.RECEIVED,
                DEFAULT_RAW_PAYLOAD,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** MAPPED 상태 InboundProduct (브랜드/카테고리 매핑 완료). */
    public static InboundProduct mappedProduct() {
        return InboundProduct.reconstitute(
                InboundProductId.of(2L),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                null,
                DEFAULT_SELLER_ID,
                InboundProductStatus.MAPPED,
                DEFAULT_RAW_PAYLOAD,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** CONVERTED 상태 InboundProduct (내부 ProductGroup 변환 완료). */
    public static InboundProduct convertedProduct() {
        return InboundProduct.reconstitute(
                InboundProductId.of(3L),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_SELLER_ID,
                InboundProductStatus.CONVERTED,
                null,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** CONVERTED 상태의 특정 ID InboundProduct. */
    public static InboundProduct convertedProduct(Long id) {
        return InboundProduct.reconstitute(
                InboundProductId.of(id),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_SELLER_ID,
                InboundProductStatus.CONVERTED,
                null,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** LEGACY_IMPORTED 상태 InboundProduct (브랜드/카테고리 매핑 있음, productGroupId 없음). */
    public static InboundProduct legacyImportedProduct() {
        return InboundProduct.reconstitute(
                InboundProductId.of(5L),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                null,
                DEFAULT_SELLER_ID,
                InboundProductStatus.LEGACY_IMPORTED,
                DEFAULT_RAW_PAYLOAD,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** LEGACY_IMPORTED 상태 InboundProduct (매핑 없음). */
    public static InboundProduct legacyImportedProductWithoutMapping() {
        return InboundProduct.reconstitute(
                InboundProductId.of(6L),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of(DEFAULT_EXTERNAL_PRODUCT_CODE),
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                InboundProductStatus.LEGACY_IMPORTED,
                DEFAULT_RAW_PAYLOAD,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** PENDING_MAPPING 상태 InboundProduct (매핑 실패). */
    public static InboundProduct pendingMappingProduct() {
        return InboundProduct.reconstitute(
                InboundProductId.of(4L),
                DEFAULT_INBOUND_SOURCE_ID,
                ExternalProductCode.of("EXT-PROD-UNMAPPED"),
                "UNKNOWN-BRAND",
                "UNKNOWN-CATEGORY",
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                InboundProductStatus.PENDING_MAPPING,
                DEFAULT_RAW_PAYLOAD,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
