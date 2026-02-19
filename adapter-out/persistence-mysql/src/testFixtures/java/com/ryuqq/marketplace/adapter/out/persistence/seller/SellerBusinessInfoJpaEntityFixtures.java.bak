package com.ryuqq.marketplace.adapter.out.persistence.seller;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SellerBusinessInfoJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerBusinessInfoJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerBusinessInfoJpaEntityFixtures {

    private SellerBusinessInfoJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_REGISTRATION_NUMBER = "123-45-67890";
    public static final String DEFAULT_COMPANY_NAME = "테스트 주식회사";
    public static final String DEFAULT_REPRESENTATIVE = "홍길동";
    public static final String DEFAULT_SALE_REPORT_NUMBER = "2024-서울강남-0001";
    public static final String DEFAULT_BUSINESS_ZIPCODE = "06141";
    public static final String DEFAULT_BUSINESS_ADDRESS = "서울시 강남구 테헤란로 123";
    public static final String DEFAULT_BUSINESS_ADDRESS_DETAIL = "테스트빌딩 5층";

    // ===== Entity Fixtures =====

    /** 활성 상태의 사업자 정보 Entity 생성. */
    public static SellerBusinessInfoJpaEntity activeEntity() {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                String.format("%03d-%02d-%05d", SEQUENCE.getAndIncrement(), 45, 67890),
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_BUSINESS_ZIPCODE,
                DEFAULT_BUSINESS_ADDRESS,
                DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 사업자 정보 Entity 생성. */
    public static SellerBusinessInfoJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                id,
                DEFAULT_SELLER_ID,
                String.format("%03d-%02d-%05d", SEQUENCE.getAndIncrement(), 45, 67890),
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_BUSINESS_ZIPCODE,
                DEFAULT_BUSINESS_ADDRESS,
                DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                null);
    }

    /** 셀러 ID를 지정한 활성 상태 사업자 정보 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SellerBusinessInfoJpaEntity activeEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                null,
                sellerId,
                String.format("%03d-%02d-%05d", SEQUENCE.getAndIncrement(), 45, 67890),
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_BUSINESS_ZIPCODE,
                DEFAULT_BUSINESS_ADDRESS,
                DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                null);
    }

    /** 삭제된 상태 사업자 정보 Entity 생성. */
    public static SellerBusinessInfoJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                String.format("%03d-%02d-%05d", SEQUENCE.getAndIncrement(), 45, 67890),
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_BUSINESS_ZIPCODE,
                DEFAULT_BUSINESS_ADDRESS,
                DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SellerBusinessInfoJpaEntity newEntity() {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                String.format("%03d-%02d-%05d", SEQUENCE.getAndIncrement(), 45, 67890),
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_BUSINESS_ZIPCODE,
                DEFAULT_BUSINESS_ADDRESS,
                DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                null);
    }

    /** 통신판매업신고번호가 없는 Entity 생성. */
    public static SellerBusinessInfoJpaEntity entityWithoutSaleReportNumber() {
        Instant now = Instant.now();
        return SellerBusinessInfoJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                String.format("%03d-%02d-%05d", SEQUENCE.getAndIncrement(), 45, 67890),
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                null,
                DEFAULT_BUSINESS_ZIPCODE,
                DEFAULT_BUSINESS_ADDRESS,
                DEFAULT_BUSINESS_ADDRESS_DETAIL,
                now,
                now,
                null);
    }
}
