package com.ryuqq.marketplace.domain.sellerapplication.aggregate;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.ContactInfo;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationAppliedEvent;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationApprovedEvent;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationRejectedEvent;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.vo.Agreement;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 셀러 입점 신청 Aggregate Root.
 *
 * <p>입점 신청 → 승인 → Seller Aggregate 생성 흐름을 관리합니다.
 */
public class SellerApplication {

    private final SellerApplicationId id;

    // 신청자 기본 정보
    private final SellerName sellerName;
    private final DisplayName displayName;
    private final LogoUrl logoUrl;
    private final Description description;

    // 사업자 정보
    private final RegistrationNumber registrationNumber;
    private final CompanyName companyName;
    private final Representative representative;
    private final SaleReportNumber saleReportNumber;
    private final Address businessAddress;

    // CS 정보
    private final CsContact csContact;

    // 담당자 연락처
    private final ContactInfo contactInfo;

    // 정산 정보
    private final BankAccount bankAccount;
    private final SettlementCycle settlementCycle;
    private final Integer settlementDay;

    // 동의 정보
    private final Agreement agreement;

    // 상태 관리
    private ApplicationStatus status;
    private final Instant appliedAt;
    private Instant processedAt;
    private String processedBy;
    private String rejectionReason;

    // 승인 후 생성된 셀러 ID
    private SellerId approvedSellerId;

    // 도메인 이벤트
    private final List<DomainEvent> events = new ArrayList<>();

    private SellerApplication(
            SellerApplicationId id,
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description,
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            CsContact csContact,
            ContactInfo contactInfo,
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            Agreement agreement,
            ApplicationStatus status,
            Instant appliedAt,
            Instant processedAt,
            String processedBy,
            String rejectionReason,
            SellerId approvedSellerId) {
        this.id = id;
        this.sellerName = sellerName;
        this.displayName = displayName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.registrationNumber = registrationNumber;
        this.companyName = companyName;
        this.representative = representative;
        this.saleReportNumber = saleReportNumber;
        this.businessAddress = businessAddress;
        this.csContact = csContact;
        this.contactInfo = contactInfo;
        this.bankAccount = bankAccount;
        this.settlementCycle = settlementCycle;
        this.settlementDay = settlementDay;
        this.agreement = agreement;
        this.status = status;
        this.appliedAt = appliedAt;
        this.processedAt = processedAt;
        this.processedBy = processedBy;
        this.rejectionReason = rejectionReason;
        this.approvedSellerId = approvedSellerId;
    }

    /**
     * 새 입점 신청 생성.
     *
     * @param sellerName 셀러명
     * @param displayName 표시명
     * @param logoUrl 로고 URL
     * @param description 설명
     * @param registrationNumber 사업자등록번호
     * @param companyName 회사명
     * @param representative 대표자명
     * @param saleReportNumber 통신판매업 신고번호
     * @param businessAddress 사업장 주소
     * @param csContact CS 연락처
     * @param contactInfo 담당자 연락처
     * @param bankAccount 정산 계좌 정보
     * @param settlementCycle 정산 주기
     * @param settlementDay 정산일
     * @param now 신청 시각
     * @return 생성된 SellerApplication
     */
    public static SellerApplication apply(
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description,
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            CsContact csContact,
            ContactInfo contactInfo,
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            Instant now) {
        SellerApplication application =
                new SellerApplication(
                        SellerApplicationId.forNew(),
                        sellerName,
                        displayName,
                        logoUrl,
                        description,
                        registrationNumber,
                        companyName,
                        representative,
                        saleReportNumber,
                        businessAddress,
                        csContact,
                        contactInfo,
                        bankAccount,
                        settlementCycle,
                        settlementDay,
                        Agreement.agreedAt(now),
                        ApplicationStatus.PENDING,
                        now,
                        null,
                        null,
                        null,
                        null);

        application.registerEvent(
                SellerApplicationAppliedEvent.of(
                        application.id(), sellerName.value(), registrationNumber.value(), now));

        return application;
    }

    /**
     * DB에서 재구성.
     *
     * @param id 신청 ID
     * @param sellerName 셀러명
     * @param displayName 표시명
     * @param logoUrl 로고 URL
     * @param description 설명
     * @param registrationNumber 사업자등록번호
     * @param companyName 회사명
     * @param representative 대표자명
     * @param saleReportNumber 통신판매업 신고번호
     * @param businessAddress 사업장 주소
     * @param csContact CS 연락처
     * @param contactInfo 담당자 연락처
     * @param bankAccount 정산 계좌 정보
     * @param settlementCycle 정산 주기
     * @param settlementDay 정산일
     * @param agreement 동의 정보
     * @param status 상태
     * @param appliedAt 신청 시각
     * @param processedAt 처리 시각
     * @param processedBy 처리자
     * @param rejectionReason 거절 사유
     * @param approvedSellerId 승인된 셀러 ID
     * @return 재구성된 SellerApplication
     */
    public static SellerApplication reconstitute(
            SellerApplicationId id,
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description,
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            CsContact csContact,
            ContactInfo contactInfo,
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            Agreement agreement,
            ApplicationStatus status,
            Instant appliedAt,
            Instant processedAt,
            String processedBy,
            String rejectionReason,
            SellerId approvedSellerId) {
        return new SellerApplication(
                id,
                sellerName,
                displayName,
                logoUrl,
                description,
                registrationNumber,
                companyName,
                representative,
                saleReportNumber,
                businessAddress,
                csContact,
                contactInfo,
                bankAccount,
                settlementCycle,
                settlementDay,
                agreement,
                status,
                appliedAt,
                processedAt,
                processedBy,
                rejectionReason,
                approvedSellerId);
    }

    public boolean isNew() {
        return id.isNew();
    }

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> polled = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(polled);
    }

    /**
     * 입점 신청 승인.
     *
     * @param approvedSellerId 승인 후 생성된 셀러 ID
     * @param processedBy 처리자 식별자 (UUIDv7 또는 이메일)
     * @param now 처리 시각
     */
    public void approve(SellerId approvedSellerId, String processedBy, Instant now) {
        validatePendingStatus();
        this.status = ApplicationStatus.APPROVED;
        this.approvedSellerId = approvedSellerId;
        this.processedBy = processedBy;
        this.processedAt = now;

        registerEvent(
                SellerApplicationApprovedEvent.of(this.id, approvedSellerId, processedBy, now));
    }

    /**
     * 입점 신청 거절.
     *
     * @param rejectionReason 거절 사유
     * @param processedBy 처리자 식별자 (UUIDv7 또는 이메일)
     * @param now 처리 시각
     */
    public void reject(String rejectionReason, String processedBy, Instant now) {
        validatePendingStatus();
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("거절 사유는 필수입니다");
        }
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.processedBy = processedBy;
        this.processedAt = now;

        registerEvent(
                SellerApplicationRejectedEvent.of(this.id, rejectionReason, processedBy, now));
    }

    private void validatePendingStatus() {
        if (!status.isPending()) {
            throw new IllegalStateException("이미 처리된 신청입니다. 현재 상태: " + status);
        }
    }

    // VO Getters
    public SellerApplicationId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerName sellerName() {
        return sellerName;
    }

    public String sellerNameValue() {
        return sellerName.value();
    }

    public DisplayName displayName() {
        return displayName;
    }

    public String displayNameValue() {
        return displayName.value();
    }

    public LogoUrl logoUrl() {
        return logoUrl;
    }

    public String logoUrlValue() {
        return logoUrl != null ? logoUrl.value() : null;
    }

    public Description description() {
        return description;
    }

    public String descriptionValue() {
        return description != null ? description.value() : null;
    }

    public RegistrationNumber registrationNumber() {
        return registrationNumber;
    }

    public String registrationNumberValue() {
        return registrationNumber.value();
    }

    public CompanyName companyName() {
        return companyName;
    }

    public String companyNameValue() {
        return companyName.value();
    }

    public Representative representative() {
        return representative;
    }

    public String representativeValue() {
        return representative.value();
    }

    public SaleReportNumber saleReportNumber() {
        return saleReportNumber;
    }

    public String saleReportNumberValue() {
        return saleReportNumber != null ? saleReportNumber.value() : null;
    }

    public Address businessAddress() {
        return businessAddress;
    }

    public CsContact csContact() {
        return csContact;
    }

    public ContactInfo contactInfo() {
        return contactInfo;
    }

    public String contactInfoName() {
        return contactInfo != null ? contactInfo.name() : null;
    }

    public String contactInfoPhone() {
        return contactInfo != null ? contactInfo.phoneValue() : null;
    }

    public String contactInfoEmail() {
        return contactInfo != null ? contactInfo.emailValue() : null;
    }

    /**
     * 정산 계좌 정보를 반환합니다.
     *
     * @return BankAccount
     */
    public BankAccount bankAccount() {
        return bankAccount;
    }

    /**
     * 은행 코드를 반환합니다.
     *
     * @return 은행 코드
     */
    public String bankCode() {
        return bankAccount != null ? bankAccount.bankCode() : null;
    }

    /**
     * 은행명을 반환합니다.
     *
     * @return 은행명
     */
    public String bankName() {
        return bankAccount != null ? bankAccount.bankName() : null;
    }

    /**
     * 계좌번호를 반환합니다.
     *
     * @return 계좌번호
     */
    public String accountNumber() {
        return bankAccount != null ? bankAccount.accountNumber() : null;
    }

    /**
     * 예금주를 반환합니다.
     *
     * @return 예금주
     */
    public String accountHolderName() {
        return bankAccount != null ? bankAccount.accountHolderName() : null;
    }

    /**
     * 정산 주기를 반환합니다.
     *
     * @return SettlementCycle
     */
    public SettlementCycle settlementCycle() {
        return settlementCycle;
    }

    /**
     * 정산일을 반환합니다.
     *
     * @return 정산일 (1-31)
     */
    public Integer settlementDay() {
        return settlementDay;
    }

    public Agreement agreement() {
        return agreement;
    }

    public ApplicationStatus status() {
        return status;
    }

    public Instant appliedAt() {
        return appliedAt;
    }

    public Instant processedAt() {
        return processedAt;
    }

    public String processedBy() {
        return processedBy;
    }

    public String rejectionReason() {
        return rejectionReason;
    }

    public SellerId approvedSellerId() {
        return approvedSellerId;
    }

    public Long approvedSellerIdValue() {
        return approvedSellerId != null ? approvedSellerId.value() : null;
    }

    public boolean isPending() {
        return status.isPending();
    }

    public boolean isApproved() {
        return status.isApproved();
    }

    public boolean isRejected() {
        return status.isRejected();
    }
}
