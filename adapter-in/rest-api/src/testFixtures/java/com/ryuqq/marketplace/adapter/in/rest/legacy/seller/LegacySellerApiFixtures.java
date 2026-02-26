package com.ryuqq.marketplace.adapter.in.rest.legacy.seller;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;

/**
 * Legacy Seller API 테스트 Fixtures.
 *
 * <p>Legacy 셀러 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacySellerApiFixtures {

    private LegacySellerApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final String DEFAULT_BIZ_NO = "123-45-67890";

    // ===== Application Result Fixtures =====

    public static LegacySellerResult sellerResult() {
        return new LegacySellerResult(DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_BIZ_NO);
    }

    public static LegacySellerResult sellerResult(long sellerId, String sellerName, String bizNo) {
        return new LegacySellerResult(sellerId, sellerName, bizNo);
    }

    // ===== API Response Fixtures =====

    public static LegacySellerResponse sellerResponse() {
        return new LegacySellerResponse(DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_BIZ_NO);
    }

    public static LegacySellerResponse sellerResponse(
            long sellerId, String sellerName, String bizNo) {
        return new LegacySellerResponse(sellerId, sellerName, bizNo);
    }
}
