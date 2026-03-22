package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite;

import jakarta.persistence.EntityManager;

/**
 * Composite Seller / Auth 통합 테스트용 데이터 셋업 헬퍼.
 *
 * <p>Seller, SellerBusinessInfo, Administrator, AdminAuthGroup, AuthGroup 엔티티는
 * 모두 읽기 전용(팩토리 메서드 없음)이므로 Native SQL로 삽입합니다.
 */
public final class LegacyCompositeSellerTestHelper {

    private final EntityManager em;

    public LegacyCompositeSellerTestHelper(EntityManager em) {
        this.em = em;
    }

    // ===== Seller 관련 =====

    /** Seller 레코드를 삽입합니다. */
    public void insertSeller(long sellerId, String sellerName) {
        insertSeller(sellerId, sellerName, null, null, 10.0);
    }

    /** Seller 레코드를 전체 필드로 삽입합니다. */
    public void insertSeller(
            long sellerId,
            String sellerName,
            String logoUrl,
            String description,
            double commissionRate) {
        em.createNativeQuery(
                        "INSERT INTO seller (seller_id, seller_name, seller_logo_url, seller_description, commission_rate) "
                                + "VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, sellerId)
                .setParameter(2, sellerName)
                .setParameter(3, logoUrl)
                .setParameter(4, description)
                .setParameter(5, commissionRate)
                .executeUpdate();
    }

    /** SellerBusinessInfo 레코드를 삽입합니다. */
    public void insertBusinessInfo(long sellerId) {
        insertBusinessInfo(
                sellerId,
                "123-45-67890",
                "테스트 주식회사",
                "홍길동",
                "2025-서울강남-0001",
                "06123",
                "서울시 강남구 테헤란로 123",
                "4층",
                "국민은행",
                "123456789012",
                "홍길동",
                "02-1234-5678",
                "010-1234-5678",
                "cs@test.com");
    }

    /** SellerBusinessInfo 레코드를 전체 필드로 삽입합니다. */
    public void insertBusinessInfo(
            long sellerId,
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            String zipCode,
            String addressLine1,
            String addressLine2,
            String bankName,
            String accountNumber,
            String accountHolderName,
            String csNumber,
            String csPhoneNumber,
            String csEmail) {
        em.createNativeQuery(
                        "INSERT INTO seller_business_info "
                                + "(seller_id, registration_number, company_name, representative, "
                                + "sale_report_number, business_address_zip_code, business_address_line1, "
                                + "business_address_line2, bank_name, account_number, account_holder_name, "
                                + "cs_number, cs_phone_number, cs_email) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, sellerId)
                .setParameter(2, registrationNumber)
                .setParameter(3, companyName)
                .setParameter(4, representative)
                .setParameter(5, saleReportNumber)
                .setParameter(6, zipCode)
                .setParameter(7, addressLine1)
                .setParameter(8, addressLine2)
                .setParameter(9, bankName)
                .setParameter(10, accountNumber)
                .setParameter(11, accountHolderName)
                .setParameter(12, csNumber)
                .setParameter(13, csPhoneNumber)
                .setParameter(14, csEmail)
                .executeUpdate();
    }

    // ===== Auth 관련 =====

    /** Administrator 레코드를 삽입합니다. */
    public void insertAdministrator(
            long adminId, long sellerId, String email, String passwordHash, String approvalStatus) {
        em.createNativeQuery(
                        "INSERT INTO administrators "
                                + "(admin_id, seller_id, email, password_hash, full_name, phone_number, approval_status, delete_yn) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
                .setParameter(1, adminId)
                .setParameter(2, sellerId)
                .setParameter(3, email)
                .setParameter(4, passwordHash)
                .setParameter(5, "테스트 관리자")
                .setParameter(6, "010-0000-0000")
                .setParameter(7, approvalStatus)
                .setParameter(8, "N")
                .executeUpdate();
    }

    /** AuthGroup 레코드를 삽입합니다. */
    public void insertAuthGroup(long authGroupId, String authGroupType) {
        em.createNativeQuery(
                        "INSERT INTO auth_group (auth_group_id, auth_group_type) VALUES (?, ?)")
                .setParameter(1, authGroupId)
                .setParameter(2, authGroupType)
                .executeUpdate();
    }

    /** AdminAuthGroup 매핑 레코드를 삽입합니다. */
    public void insertAdminAuthGroup(long adminId, long authGroupId) {
        em.createNativeQuery(
                        "INSERT INTO admin_auth_group (admin_id, auth_group_id, delete_yn) VALUES (?, ?, ?)")
                .setParameter(1, adminId)
                .setParameter(2, authGroupId)
                .setParameter(3, "N")
                .executeUpdate();
    }

    /**
     * 셀러 + 사업자 정보 풀 데이터 셋업.
     *
     * @return sellerId
     */
    public long setupFullSellerData() {
        long sellerId = 10L;
        insertSeller(sellerId, "테스트 셀러", "https://cdn.example.com/logo.png", "셀러 설명", 15.5);
        insertBusinessInfo(sellerId);
        flushAndClear();
        return sellerId;
    }

    /**
     * 셀러 + 인증 풀 데이터 셋업.
     *
     * @return email
     */
    public String setupFullAuthData() {
        long sellerId = 10L;
        long adminId = 1L;
        long authGroupId = 1L;
        String email = "admin@test.com";

        insertSeller(sellerId, "테스트 셀러");
        insertAdministrator(adminId, sellerId, email, "$2a$10$hashvalue", "APPROVED");
        insertAuthGroup(authGroupId, "MASTER");
        insertAdminAuthGroup(adminId, authGroupId);
        flushAndClear();
        return email;
    }

    /** flush + clear로 1차 캐시를 비웁니다. */
    public void flushAndClear() {
        em.flush();
        em.clear();
    }
}
