package com.ryuqq.marketplace.application.legacy.seller;

import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * LegacySeller Application Query 테스트 Fixtures.
 *
 * <p>LegacySeller 관련 Query 결과 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacySellerQueryFixtures {

    private LegacySellerQueryFixtures() {}

    // ===== SellerAdminCompositeResult Fixtures =====

    public static SellerAdminCompositeResult sellerAdminCompositeResult(long sellerId) {
        Instant now = Instant.parse("2026-03-20T00:00:00Z");
        return new SellerAdminCompositeResult(
                sellerInfo(sellerId, now),
                businessInfo(),
                csInfo(),
                contractInfo(now),
                settlementInfo(now));
    }

    public static SellerAdminCompositeResult.SellerInfo sellerInfo(long sellerId, Instant now) {
        return new SellerAdminCompositeResult.SellerInfo(
                sellerId,
                "레거시 테스트 셀러",
                "레거시 테스트 스토어",
                "http://example.com/legacy-logo.png",
                "레거시 셀러 설명",
                true,
                now,
                now);
    }

    public static SellerAdminCompositeResult.BusinessInfo businessInfo() {
        return new SellerAdminCompositeResult.BusinessInfo(
                1L,
                "123-45-67890",
                "레거시 테스트 주식회사",
                "홍길동",
                "2024-서울강남-0001",
                "06141",
                "서울시 강남구",
                "테스트빌딩");
    }

    public static SellerAdminCompositeResult.CsInfo csInfo() {
        return new SellerAdminCompositeResult.CsInfo(
                1L,
                "02-1234-5678",
                "010-1234-5678",
                "cs@legacy-test.com",
                "09:00",
                "18:00",
                "MON,TUE,WED,THU,FRI",
                "https://kakao.legacy.test");
    }

    public static SellerAdminCompositeResult.ContractInfo contractInfo(Instant now) {
        return new SellerAdminCompositeResult.ContractInfo(
                1L,
                BigDecimal.valueOf(10.0),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "ACTIVE",
                null,
                now,
                now);
    }

    public static SellerAdminCompositeResult.SettlementInfo settlementInfo(Instant now) {
        return new SellerAdminCompositeResult.SettlementInfo(
                1L,
                "088",
                "신한은행",
                "123-456-789",
                "레거시 테스트 주식회사",
                "MONTHLY",
                15,
                true,
                now,
                now,
                now);
    }
}
