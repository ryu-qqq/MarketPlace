package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.time.Instant;

/**
 * SellerApplicationJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerApplicationJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerApplicationJpaEntityFixtures {

    private SellerApplicationJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_SELLER_NAME = "테스트셀러";
    public static final String DEFAULT_DISPLAY_NAME = "테스트 브랜드";
    public static final String DEFAULT_LOGO_URL = "https://example.com/logo.png";
    public static final String DEFAULT_DESCRIPTION = "테스트 셀러 설명입니다.";
    public static final String DEFAULT_REGISTRATION_NUMBER = "123-45-67890";
    public static final String DEFAULT_COMPANY_NAME = "테스트컴퍼니";
    public static final String DEFAULT_REPRESENTATIVE = "홍길동";
    public static final String DEFAULT_SALE_REPORT_NUMBER = "제2025-서울강남-1234호";
    public static final String DEFAULT_ZIP_CODE = "12345";
    public static final String DEFAULT_BASE_ADDRESS = "서울시 강남구";
    public static final String DEFAULT_DETAIL_ADDRESS = "테헤란로 123";
    public static final String DEFAULT_CS_PHONE = "02-1234-5678";
    public static final String DEFAULT_CS_EMAIL = "cs@example.com";
    public static final String DEFAULT_CONTACT_NAME = "김담당";
    public static final String DEFAULT_CONTACT_PHONE = "010-9876-5432";
    public static final String DEFAULT_CONTACT_EMAIL = "contact@example.com";
    public static final String DEFAULT_PROCESSED_BY = "admin@example.com";
    public static final String DEFAULT_REJECTION_REASON = "서류 미비로 인한 반려";

    // ===== 정산 정보 상수 =====
    public static final String DEFAULT_BANK_CODE = "088";
    public static final String DEFAULT_BANK_NAME = "신한은행";
    public static final String DEFAULT_ACCOUNT_NUMBER = "110123456789";
    public static final String DEFAULT_ACCOUNT_HOLDER_NAME = "홍길동";
    public static final String DEFAULT_SETTLEMENT_CYCLE = "MONTHLY";
    public static final Integer DEFAULT_SETTLEMENT_DAY = 1;

    // ===== Entity Fixtures =====

    /** 대기(PENDING) 상태의 입점 신청 Entity 생성. ID가 null인 새로운 Entity. */
    public static SellerApplicationJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                null,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                DEFAULT_REGISTRATION_NUMBER,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                now,
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /**
     * 지정된 ID와 사업자등록번호로 대기 상태 Entity 생성.
     *
     * @param id 신청 ID (persist 시 null로 변환)
     * @param registrationNumber 사업자등록번호
     * @return 대기 상태 Entity
     */
    public static SellerApplicationJpaEntity pendingEntity(Long id, String registrationNumber) {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                id,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                registrationNumber,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                now,
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /**
     * 지정된 셀러명으로 대기 상태 Entity 생성.
     *
     * @param sellerName 셀러명
     * @param companyName 회사명
     * @return 대기 상태 Entity
     */
    public static SellerApplicationJpaEntity pendingEntityWithName(
            String sellerName, String companyName) {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                null,
                sellerName,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                DEFAULT_REGISTRATION_NUMBER,
                companyName,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                now,
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /**
     * 승인(APPROVED) 상태의 입점 신청 Entity 생성.
     *
     * @param sellerId 승인된 셀러 ID
     * @return 승인 상태 Entity (사업자등록번호는 sellerId 기반으로 고유하게 생성)
     */
    public static SellerApplicationJpaEntity approvedEntity(Long sellerId) {
        Instant now = Instant.now();
        Instant appliedAt = now.minusSeconds(3600);
        return SellerApplicationJpaEntity.create(
                null,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                "999-"
                        + String.format("%02d", sellerId % 100)
                        + "-"
                        + String.format("%05d", sellerId), // 고유 사업자등록번호
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                appliedAt,
                ApplicationStatus.APPROVED,
                appliedAt,
                now,
                DEFAULT_PROCESSED_BY,
                null,
                sellerId,
                now,
                now);
    }

    /**
     * 거절(REJECTED) 상태의 입점 신청 Entity 생성.
     *
     * @return 거절 상태 Entity
     */
    public static SellerApplicationJpaEntity rejectedEntity() {
        Instant now = Instant.now();
        Instant appliedAt = now.minusSeconds(3600);
        return SellerApplicationJpaEntity.create(
                null,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                DEFAULT_REGISTRATION_NUMBER,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                appliedAt,
                ApplicationStatus.REJECTED,
                appliedAt,
                now,
                DEFAULT_PROCESSED_BY,
                DEFAULT_REJECTION_REASON,
                null,
                now,
                now);
    }

    /**
     * 거절(REJECTED) 상태의 입점 신청 Entity 생성 (커스텀 거절 사유).
     *
     * @param rejectionReason 거절 사유
     * @return 거절 상태 Entity
     */
    public static SellerApplicationJpaEntity rejectedEntity(String rejectionReason) {
        Instant now = Instant.now();
        Instant appliedAt = now.minusSeconds(3600);
        return SellerApplicationJpaEntity.create(
                null,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                DEFAULT_REGISTRATION_NUMBER,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                appliedAt,
                ApplicationStatus.REJECTED,
                appliedAt,
                now,
                DEFAULT_PROCESSED_BY,
                rejectionReason,
                null,
                now,
                now);
    }

    /**
     * 커스텀 사업자등록번호로 대기 상태 Entity 생성.
     *
     * @param registrationNumber 사업자등록번호
     * @return 대기 상태 Entity
     */
    public static SellerApplicationJpaEntity pendingEntityWithRegistrationNumber(
            String registrationNumber) {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                null,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                registrationNumber,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                now,
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /**
     * 커스텀 회사명, 대표자명, 사업자등록번호로 대기 상태 Entity 생성.
     *
     * @param companyName 회사명
     * @param representative 대표자명
     * @param registrationNumber 사업자등록번호
     * @return 대기 상태 Entity
     */
    public static SellerApplicationJpaEntity pendingEntityWithFullInfo(
            String companyName, String representative, String registrationNumber) {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                null,
                DEFAULT_SELLER_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                registrationNumber,
                companyName,
                representative,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                now,
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /**
     * 커스텀 셀러명과 사업자등록번호로 대기 상태 Entity 생성.
     *
     * @param sellerName 셀러명
     * @param registrationNumber 사업자등록번호
     * @return 대기 상태 Entity
     */
    public static SellerApplicationJpaEntity pendingEntityWithCustomInfo(
            String sellerName, String registrationNumber) {
        Instant now = Instant.now();
        return SellerApplicationJpaEntity.create(
                null,
                sellerName,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_LOGO_URL,
                DEFAULT_DESCRIPTION,
                registrationNumber,
                DEFAULT_COMPANY_NAME,
                DEFAULT_REPRESENTATIVE,
                DEFAULT_SALE_REPORT_NUMBER,
                DEFAULT_ZIP_CODE,
                DEFAULT_BASE_ADDRESS,
                DEFAULT_DETAIL_ADDRESS,
                DEFAULT_CS_PHONE,
                DEFAULT_CS_EMAIL,
                DEFAULT_CONTACT_NAME,
                DEFAULT_CONTACT_PHONE,
                DEFAULT_CONTACT_EMAIL,
                DEFAULT_BANK_CODE,
                DEFAULT_BANK_NAME,
                DEFAULT_ACCOUNT_NUMBER,
                DEFAULT_ACCOUNT_HOLDER_NAME,
                DEFAULT_SETTLEMENT_CYCLE,
                DEFAULT_SETTLEMENT_DAY,
                now,
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null,
                now,
                now);
    }
}
