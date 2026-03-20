package com.ryuqq.marketplace.adapter.in.rest.legacy.seller;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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

    public static SellerAdminCompositeResult sellerAdminCompositeResult() {
        return sellerAdminCompositeResult(DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME, DEFAULT_BIZ_NO);
    }

    public static SellerAdminCompositeResult sellerAdminCompositeResult(
            long sellerId, String sellerName, String bizNo) {
        return new SellerAdminCompositeResult(
                new SellerAdminCompositeResult.SellerInfo(
                        sellerId, sellerName, sellerName, "", "", true, Instant.now(), Instant.now()),
                new SellerAdminCompositeResult.BusinessInfo(
                        1L, bizNo, "테스트 회사", "대표자", "2024-통신-0001", "12345", "서울시", "강남구"),
                new SellerAdminCompositeResult.CsInfo(
                        1L, "02-1234-5678", "010-1234-5678", "cs@test.com", "09:00", "18:00", "월~금", ""),
                new SellerAdminCompositeResult.ContractInfo(
                        1L, BigDecimal.TEN, LocalDate.now(), LocalDate.now().plusYears(1),
                        "ACTIVE", "", Instant.now(), Instant.now()),
                new SellerAdminCompositeResult.SettlementInfo(
                        1L, "004", "국민은행", "1234567890", "테스트", "MONTHLY",
                        15, true, Instant.now(), Instant.now(), Instant.now()));
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
