package com.ryuqq.marketplace.domain.seller;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContractUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlementUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerAuthOutboxId;
import com.ryuqq.marketplace.domain.seller.id.SellerBusinessInfoId;
import com.ryuqq.marketplace.domain.seller.id.SellerContractId;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.id.SellerSettlementId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.CommissionRate;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.ContactInfo;
import com.ryuqq.marketplace.domain.seller.vo.ContractStatus;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import com.ryuqq.marketplace.domain.seller.vo.SellerAuthOutboxStatus;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Seller 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Seller 관련 객체들을 생성합니다.
 */
public final class SellerFixtures {

    private SellerFixtures() {}

    // ===== SellerName Fixtures =====
    public static SellerName sellerName(String value) {
        return SellerName.of(value);
    }

    public static SellerName defaultSellerName() {
        return SellerName.of("테스트 셀러");
    }

    // ===== DisplayName Fixtures =====
    public static DisplayName displayName(String value) {
        return DisplayName.of(value);
    }

    public static DisplayName defaultDisplayName() {
        return DisplayName.of("테스트 셀러 스토어");
    }

    // ===== LogoUrl Fixtures =====
    public static LogoUrl logoUrl(String value) {
        return LogoUrl.of(value);
    }

    public static LogoUrl defaultLogoUrl() {
        return LogoUrl.of("https://example.com/logo.png");
    }

    public static LogoUrl emptyLogoUrl() {
        return LogoUrl.empty();
    }

    // ===== Description Fixtures =====
    public static Description description(String value) {
        return Description.of(value);
    }

    public static Description defaultDescription() {
        return Description.of("테스트 셀러 설명입니다.");
    }

    public static Description emptyDescription() {
        return Description.empty();
    }

    // ===== Seller Aggregate Fixtures =====
    public static Seller newSeller() {
        return Seller.forNew(
                defaultSellerName(),
                defaultDisplayName(),
                defaultLogoUrl(),
                defaultDescription(),
                CommonVoFixtures.now());
    }

    public static Seller newSeller(SellerName name, DisplayName displayName) {
        return Seller.forNew(
                name, displayName, defaultLogoUrl(), defaultDescription(), CommonVoFixtures.now());
    }

    public static Seller activeSeller() {
        return Seller.reconstitute(
                SellerId.of(1L),
                defaultSellerName(),
                defaultDisplayName(),
                defaultLogoUrl(),
                defaultDescription(),
                true,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Seller activeSeller(Long id) {
        return Seller.reconstitute(
                SellerId.of(id),
                defaultSellerName(),
                defaultDisplayName(),
                defaultLogoUrl(),
                defaultDescription(),
                true,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Seller activeSellerWithAuth(Long id) {
        return Seller.reconstitute(
                SellerId.of(id),
                defaultSellerName(),
                defaultDisplayName(),
                defaultLogoUrl(),
                defaultDescription(),
                true,
                null,
                "tenant-123",
                "org-456",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Seller inactiveSeller() {
        return Seller.reconstitute(
                SellerId.of(2L),
                defaultSellerName(),
                defaultDisplayName(),
                defaultLogoUrl(),
                defaultDescription(),
                false,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Seller deletedSeller() {
        Instant deletedAt = CommonVoFixtures.yesterday();
        return Seller.reconstitute(
                SellerId.of(3L),
                defaultSellerName(),
                defaultDisplayName(),
                defaultLogoUrl(),
                defaultDescription(),
                false,
                deletedAt,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== SellerUpdateData Fixtures =====
    public static SellerUpdateData sellerUpdateData() {
        return SellerUpdateData.of(
                SellerName.of("수정된 셀러명"),
                DisplayName.of("수정된 노출명"),
                LogoUrl.of("https://example.com/new-logo.png"),
                Description.of("수정된 설명입니다."));
    }

    public static SellerUpdateData sellerUpdateData(
            String sellerName, String displayName, String logoUrl, String description) {
        return SellerUpdateData.of(sellerName, displayName, logoUrl, description);
    }

    // ===== SellerBusinessInfo Fixtures =====
    public static RegistrationNumber defaultRegistrationNumber() {
        return RegistrationNumber.of("123-45-67890");
    }

    public static CompanyName defaultCompanyName() {
        return CompanyName.of("테스트 주식회사");
    }

    public static Representative defaultRepresentative() {
        return Representative.of("홍길동");
    }

    public static SaleReportNumber defaultSaleReportNumber() {
        return SaleReportNumber.of("2024-서울강남-0001");
    }

    public static Address defaultBusinessAddress() {
        return Address.of("06141", "서울시 강남구 테헤란로 123", "테스트빌딩 5층");
    }

    public static CsContact defaultCsContact() {
        return CsContact.of("02-1234-5678", "010-1234-5678", "cs@test.com");
    }

    public static SellerBusinessInfo newSellerBusinessInfo() {
        return SellerBusinessInfo.forNew(
                CommonVoFixtures.defaultSellerId(),
                defaultRegistrationNumber(),
                defaultCompanyName(),
                defaultRepresentative(),
                defaultSaleReportNumber(),
                defaultBusinessAddress(),
                CommonVoFixtures.now());
    }

    public static SellerBusinessInfo newSellerBusinessInfo(SellerId sellerId) {
        return SellerBusinessInfo.forNew(
                sellerId,
                defaultRegistrationNumber(),
                defaultCompanyName(),
                defaultRepresentative(),
                defaultSaleReportNumber(),
                defaultBusinessAddress(),
                CommonVoFixtures.now());
    }

    public static SellerBusinessInfo activeSellerBusinessInfo() {
        return SellerBusinessInfo.reconstitute(
                SellerBusinessInfoId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultRegistrationNumber(),
                defaultCompanyName(),
                defaultRepresentative(),
                defaultSaleReportNumber(),
                defaultBusinessAddress(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SellerBusinessInfo deletedSellerBusinessInfo() {
        return SellerBusinessInfo.reconstitute(
                SellerBusinessInfoId.of(2L),
                CommonVoFixtures.defaultSellerId(),
                defaultRegistrationNumber(),
                defaultCompanyName(),
                defaultRepresentative(),
                defaultSaleReportNumber(),
                defaultBusinessAddress(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SellerBusinessInfoUpdateData sellerBusinessInfoUpdateData() {
        return SellerBusinessInfoUpdateData.of(
                RegistrationNumber.of("987-65-43210"),
                CompanyName.of("수정된 주식회사"),
                Representative.of("김철수"),
                SaleReportNumber.of("2024-서울강남-0002"),
                Address.of("06142", "서울시 강남구 역삼로 456", "수정빌딩 10층"));
    }

    // ===== ContactInfo Fixtures =====
    public static ContactInfo defaultContactInfo() {
        return ContactInfo.of("김담당", "010-9876-5432", "kim@test.com");
    }

    // ===== SellerCs Fixtures =====
    public static SellerCs newSellerCs() {
        return SellerCs.forNew(
                defaultCsContact(),
                OperatingHours.businessHours(),
                "MON,TUE,WED,THU,FRI",
                null,
                CommonVoFixtures.now());
    }

    public static SellerCs newSellerCs(SellerId sellerId) {
        return SellerCs.forNew(
                sellerId,
                defaultCsContact(),
                OperatingHours.businessHours(),
                "MON,TUE,WED,THU,FRI",
                null,
                CommonVoFixtures.now());
    }

    public static SellerCs activeSellerCs() {
        return SellerCs.reconstitute(
                SellerCsId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultCsContact(),
                OperatingHours.businessHours(),
                "MON,TUE,WED,THU,FRI",
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SellerCsUpdateData sellerCsUpdateData() {
        return SellerCsUpdateData.of(
                CsContact.of("02-9999-8888", "010-9999-8888", "updated-cs@test.com"),
                OperatingHours.businessHours(),
                "월,화,수,목,금",
                "https://pf.kakao.com/updated");
    }

    // ===== SellerContract Fixtures =====
    public static CommissionRate defaultCommissionRate() {
        return CommissionRate.of(BigDecimal.valueOf(10.0));
    }

    public static SellerContract newSellerContract() {
        return SellerContract.forNew(
                defaultCommissionRate(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "특약사항 없음",
                CommonVoFixtures.now());
    }

    public static SellerContract newSellerContract(SellerId sellerId) {
        return SellerContract.forNew(
                sellerId,
                defaultCommissionRate(),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "특약사항 없음",
                CommonVoFixtures.now());
    }

    public static SellerContract activeSellerContract() {
        return SellerContract.reconstitute(
                SellerContractId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultCommissionRate(),
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(11),
                ContractStatus.ACTIVE,
                "특약사항 없음",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SellerContractUpdateData sellerContractUpdateData() {
        return SellerContractUpdateData.of(
                CommissionRate.of(BigDecimal.valueOf(15.0)),
                LocalDate.now(),
                LocalDate.now().plusYears(2),
                "수정된 특약사항");
    }

    // ===== SellerSettlement Fixtures =====
    public static BankAccount defaultBankAccount() {
        return BankAccount.of("004", "국민은행", "12345678901234", "홍길동");
    }

    public static SellerSettlement newSellerSettlement() {
        return SellerSettlement.forNew(
                defaultBankAccount(), SettlementCycle.MONTHLY, 15, CommonVoFixtures.now());
    }

    public static SellerSettlement newSellerSettlement(SellerId sellerId) {
        return SellerSettlement.forNew(
                sellerId,
                defaultBankAccount(),
                SettlementCycle.MONTHLY,
                15,
                CommonVoFixtures.now());
    }

    public static SellerSettlement activeSellerSettlement() {
        return SellerSettlement.reconstitute(
                SellerSettlementId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultBankAccount(),
                SettlementCycle.MONTHLY,
                15,
                true,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SellerSettlementUpdateData sellerSettlementUpdateData() {
        return SellerSettlementUpdateData.of(
                BankAccount.of("088", "신한은행", "98765432101234", "김철수"),
                SettlementCycle.BIWEEKLY,
                1);
    }

    // ===== SellerAuthOutbox Fixtures =====
    private static final String IDEMPOTENCY_KEY_PREFIX = "SAO";

    public static String defaultAuthOutboxPayload() {
        return "{\"sellerName\":\"테스트 셀러\",\"companyName\":\"테스트 주식회사\"}";
    }

    private static String generateIdempotencyKey(Long sellerId, Instant createdAt) {
        long sellerIdValue = sellerId != null ? sellerId : 0L;
        return IDEMPOTENCY_KEY_PREFIX + ":" + sellerIdValue + ":" + createdAt.toEpochMilli();
    }

    public static SellerAuthOutbox newSellerAuthOutbox() {
        return SellerAuthOutbox.forNew(defaultAuthOutboxPayload(), CommonVoFixtures.now());
    }

    public static SellerAuthOutbox newSellerAuthOutbox(SellerId sellerId) {
        return SellerAuthOutbox.forNew(
                sellerId, defaultAuthOutboxPayload(), CommonVoFixtures.now());
    }

    public static SellerAuthOutbox pendingSellerAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.PENDING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAuthOutbox pendingSellerAuthOutboxWithId() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.PENDING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAuthOutbox processingSellerAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(2L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.PROCESSING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAuthOutbox completedSellerAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(3L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.COMPLETED,
                0,
                3,
                yesterday,
                now,
                now,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAuthOutbox failedSellerAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(4L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.FAILED,
                3,
                3,
                yesterday,
                now,
                now,
                "인증 서버 연결 실패",
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAuthOutbox retriableSellerAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(5L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.PENDING,
                1,
                3,
                yesterday,
                yesterday,
                null,
                "첫 번째 시도 실패",
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAuthOutbox processingTimeoutSellerAuthOutbox(long secondsAgo) {
        Instant createdAt = CommonVoFixtures.now().minusSeconds(secondsAgo);
        return SellerAuthOutbox.reconstitute(
                SellerAuthOutboxId.of(6L),
                CommonVoFixtures.defaultSellerId(),
                defaultAuthOutboxPayload(),
                SellerAuthOutboxStatus.PROCESSING,
                0,
                3,
                createdAt,
                createdAt,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), createdAt));
    }

    // ===== SellerAdmin Fixtures =====
    private static final String DEFAULT_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    private static final String ACTIVE_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f61";
    private static final String REJECTED_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f62";
    private static final String SUSPENDED_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f63";

    public static SellerAdmin newSellerAdmin() {
        return SellerAdmin.forNew(
                SellerAdminId.forNew(DEFAULT_SELLER_ADMIN_ID),
                CommonVoFixtures.defaultSellerId(),
                "auth-user-123",
                LoginId.of("admin@test.com"),
                AdminName.of("홍길동"),
                CommonVoFixtures.defaultPhoneNumber(),
                CommonVoFixtures.now());
    }

    public static SellerAdmin newSellerAdminWithoutSellerId() {
        return SellerAdmin.forNew(
                SellerAdminId.forNew(DEFAULT_SELLER_ADMIN_ID),
                "auth-user-123",
                LoginId.of("admin@test.com"),
                AdminName.of("홍길동"),
                CommonVoFixtures.defaultPhoneNumber(),
                CommonVoFixtures.now());
    }

    public static SellerAdmin pendingApprovalSellerAdmin() {
        return SellerAdmin.forApplication(
                SellerAdminId.forNew(DEFAULT_SELLER_ADMIN_ID),
                CommonVoFixtures.defaultSellerId(),
                LoginId.of("pending@test.com"),
                AdminName.of("김대기"),
                CommonVoFixtures.phoneNumber("010-9999-8888"),
                CommonVoFixtures.now());
    }

    public static SellerAdmin activeSellerAdmin() {
        return SellerAdmin.reconstitute(
                SellerAdminId.of(ACTIVE_SELLER_ADMIN_ID),
                CommonVoFixtures.defaultSellerId(),
                "auth-user-456",
                LoginId.of("active@test.com"),
                AdminName.of("이활성"),
                CommonVoFixtures.phoneNumber("010-1111-2222"),
                SellerAdminStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static SellerAdmin rejectedSellerAdmin() {
        return SellerAdmin.reconstitute(
                SellerAdminId.of(REJECTED_SELLER_ADMIN_ID),
                CommonVoFixtures.defaultSellerId(),
                null,
                LoginId.of("rejected@test.com"),
                AdminName.of("박거절"),
                CommonVoFixtures.phoneNumber("010-3333-4444"),
                SellerAdminStatus.REJECTED,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }

    public static SellerAdmin suspendedSellerAdmin() {
        return SellerAdmin.reconstitute(
                SellerAdminId.of(SUSPENDED_SELLER_ADMIN_ID),
                CommonVoFixtures.defaultSellerId(),
                "auth-user-789",
                LoginId.of("suspended@test.com"),
                AdminName.of("최정지"),
                CommonVoFixtures.phoneNumber("010-5555-6666"),
                SellerAdminStatus.SUSPENDED,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null);
    }
}
