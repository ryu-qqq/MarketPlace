package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * SellerCompositeDtoFixtures - 셀러 Composite DTO 테스트 Fixtures.
 *
 * <p>Composite Query 테스트용 DTO 생성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class SellerCompositeDtoFixtures {

    private SellerCompositeDtoFixtures() {}

    // ========================================================================
    // SellerCompositeDto Fixtures
    // ========================================================================

    /** 기본 활성 상태 셀러 Composite DTO */
    public static SellerCompositeDto activeSellerCompositeDto() {
        return activeSellerCompositeDto(1L);
    }

    /** 지정 ID의 활성 상태 셀러 Composite DTO */
    public static SellerCompositeDto activeSellerCompositeDto(Long sellerId) {
        return new SellerCompositeDto(
                sellerId,
                "테스트셀러",
                "테스트 디스플레이명",
                "https://logo.example.com/logo.png",
                "테스트 셀러 설명입니다.",
                true,
                Instant.now().minusSeconds(86400),
                Instant.now(),
                // BusinessInfo
                200L,
                "123-45-67890",
                "(주)테스트회사",
                "홍길동",
                "2024-서울강남-0001",
                "06234",
                "서울시 강남구 역삼동",
                "456호",
                // SellerCs
                300L,
                "02-1234-5678",
                "010-9876-5432",
                "cs@test.com",
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                "MON,TUE,WED,THU,FRI",
                "https://pf.kakao.com/test");
    }

    /** 비활성 상태 셀러 Composite DTO */
    public static SellerCompositeDto inactiveSellerCompositeDto() {
        return new SellerCompositeDto(
                2L,
                "비활성셀러",
                "비활성 디스플레이명",
                null,
                null,
                false,
                Instant.now().minusSeconds(172800),
                Instant.now().minusSeconds(86400),
                // BusinessInfo
                201L,
                "987-65-43210",
                "(주)비활성회사",
                "김철수",
                null,
                "13595",
                "경기도 성남시 분당구 정자동",
                "101호",
                // SellerCs
                301L,
                "031-9876-5432",
                null,
                "inactive@test.com",
                null,
                null,
                null,
                null);
    }

    // ========================================================================
    // SellerPolicyCompositeDto Fixtures
    // ========================================================================

    /** 기본 정책 Composite DTO (배송/환불 정책 포함) */
    public static SellerPolicyCompositeDto sellerPolicyCompositeDto() {
        return sellerPolicyCompositeDto(1L);
    }

    /** 지정 셀러 ID의 정책 Composite DTO */
    public static SellerPolicyCompositeDto sellerPolicyCompositeDto(Long sellerId) {
        return new SellerPolicyCompositeDto(
                sellerId,
                List.of(defaultShippingPolicyDto(sellerId), secondShippingPolicyDto(sellerId)),
                List.of(defaultRefundPolicyDto(sellerId)));
    }

    /** 빈 정책 Composite DTO */
    public static SellerPolicyCompositeDto emptyPolicyCompositeDto(Long sellerId) {
        return new SellerPolicyCompositeDto(sellerId, List.of(), List.of());
    }

    // ========================================================================
    // ShippingPolicyDto Fixtures
    // ========================================================================

    /** 기본 배송 정책 DTO */
    public static ShippingPolicyDto defaultShippingPolicyDto(Long sellerId) {
        return new ShippingPolicyDto(
                1L,
                sellerId,
                "기본 배송 정책",
                true,
                true,
                "CONDITIONAL_FREE",
                3000,
                50000,
                3000,
                5000,
                3000,
                6000,
                1,
                3,
                LocalTime.of(14, 0),
                Instant.now().minusSeconds(86400),
                Instant.now());
    }

    /** 두 번째 배송 정책 DTO */
    public static ShippingPolicyDto secondShippingPolicyDto(Long sellerId) {
        return new ShippingPolicyDto(
                2L,
                sellerId,
                "빠른 배송 정책",
                false,
                true,
                "FIXED",
                5000,
                null,
                5000,
                7000,
                5000,
                10000,
                1,
                2,
                LocalTime.of(18, 0),
                Instant.now().minusSeconds(43200),
                Instant.now());
    }

    /** 비활성 배송 정책 DTO */
    public static ShippingPolicyDto inactiveShippingPolicyDto(Long sellerId) {
        return new ShippingPolicyDto(
                3L,
                sellerId,
                "사용중지 배송 정책",
                false,
                false,
                "FREE",
                0,
                0,
                0,
                0,
                0,
                0,
                3,
                5,
                null,
                Instant.now().minusSeconds(172800),
                Instant.now().minusSeconds(86400));
    }

    // ========================================================================
    // RefundPolicyDto Fixtures
    // ========================================================================

    /** 기본 환불 정책 DTO */
    public static RefundPolicyDto defaultRefundPolicyDto(Long sellerId) {
        return new RefundPolicyDto(
                1L,
                sellerId,
                "기본 환불 정책",
                true,
                true,
                7,
                14,
                "상품 훼손 시 반품 불가, 착용 흔적 있을 경우 반품 불가",
                true,
                false,
                3,
                "추가 안내: 단순 변심에 의한 반품 시 배송비는 고객 부담입니다.",
                Instant.now().minusSeconds(86400),
                Instant.now());
    }

    /** 엄격한 환불 정책 DTO */
    public static RefundPolicyDto strictRefundPolicyDto(Long sellerId) {
        return new RefundPolicyDto(
                2L,
                sellerId,
                "엄격 환불 정책",
                false,
                true,
                3,
                7,
                "모든 개봉 상품 반품 불가",
                false,
                true,
                5,
                "검수 필수",
                Instant.now().minusSeconds(43200),
                Instant.now());
    }

    // ========================================================================
    // SellerAdminCompositeDto Fixtures
    // ========================================================================

    /** 기본 Admin Composite DTO (전체 정보 포함) */
    public static SellerAdminCompositeDto activeSellerAdminCompositeDto() {
        return activeSellerAdminCompositeDto(1L);
    }

    /** 지정 ID의 Admin Composite DTO */
    public static SellerAdminCompositeDto activeSellerAdminCompositeDto(Long sellerId) {
        Instant now = Instant.now();
        return new SellerAdminCompositeDto(
                sellerId,
                "테스트셀러",
                "테스트 디스플레이명",
                "https://logo.example.com/logo.png",
                "테스트 셀러 설명입니다.",
                true,
                now.minusSeconds(86400),
                now,
                // SellerBusinessInfo
                200L,
                "123-45-67890",
                "(주)테스트회사",
                "홍길동",
                "2024-서울강남-0001",
                "06234",
                "서울시 강남구 역삼동",
                "456호",
                // SellerCs
                300L,
                "02-1234-5678",
                "010-9876-5432",
                "cs@test.com",
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                "MON,TUE,WED,THU,FRI",
                "https://pf.kakao.com/test",
                // SellerContract
                400L,
                new BigDecimal("5.00"),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "ACTIVE",
                "특별 약관 내용",
                now.minusSeconds(86400),
                now,
                // SellerSettlement
                500L,
                "004",
                "KB국민은행",
                "12345678901234",
                "홍길동",
                "MONTHLY",
                25,
                true,
                now.minusSeconds(3600),
                now.minusSeconds(86400),
                now);
    }

    /** 비활성 상태 Admin Composite DTO */
    public static SellerAdminCompositeDto inactiveSellerAdminCompositeDto() {
        Instant now = Instant.now();
        return new SellerAdminCompositeDto(
                2L,
                "비활성셀러",
                "비활성 디스플레이명",
                null,
                null,
                false,
                now.minusSeconds(172800),
                now.minusSeconds(86400),
                // SellerBusinessInfo
                201L,
                "987-65-43210",
                "(주)비활성회사",
                "김철수",
                null,
                "13595",
                "경기도 성남시 분당구 정자동",
                "101호",
                // SellerCs
                301L,
                "031-9876-5432",
                null,
                "inactive@test.com",
                null,
                null,
                null,
                null,
                // SellerContract
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                // SellerSettlement
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null);
    }
}
