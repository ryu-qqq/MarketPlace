package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.id.SellerSettlementId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import java.time.Instant;

/**
 * 셀러 정산 정보 Entity.
 *
 * <p>Seller와 1:1 관계. 정산 계좌, 정산 주기 등을 관리합니다.
 */
public class SellerSettlement {

    private final SellerSettlementId id;
    private SellerId sellerId;
    private BankAccount bankAccount;
    private SettlementCycle settlementCycle;
    private Integer settlementDay;
    private boolean verified;
    private Instant verifiedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private SellerSettlement(
            SellerSettlementId id,
            SellerId sellerId,
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            boolean verified,
            Instant verifiedAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.bankAccount = bankAccount;
        this.settlementCycle = settlementCycle;
        this.settlementDay = settlementDay;
        this.verified = verified;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** SellerId 없이 새 SellerSettlement 생성. */
    public static SellerSettlement forNew(
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            Instant now) {
        return new SellerSettlement(
                SellerSettlementId.forNew(),
                null,
                bankAccount,
                settlementCycle,
                settlementDay,
                false,
                null,
                now,
                now);
    }

    /** SellerId와 함께 새 SellerSettlement 생성. */
    public static SellerSettlement forNew(
            SellerId sellerId,
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            Instant now) {
        return new SellerSettlement(
                SellerSettlementId.forNew(),
                sellerId,
                bankAccount,
                settlementCycle,
                settlementDay,
                false,
                null,
                now,
                now);
    }

    /** 기본 정산 정보 생성 (계좌만 지정, 월간 정산). */
    public static SellerSettlement defaultSettlement(
            SellerId sellerId, BankAccount bankAccount, Instant now) {
        return new SellerSettlement(
                SellerSettlementId.forNew(),
                sellerId,
                bankAccount,
                SettlementCycle.MONTHLY,
                1,
                false,
                null,
                now,
                now);
    }

    /** 대기 상태 정산 정보 생성 (계좌 미등록). */
    public static SellerSettlement forPending(SellerId sellerId, Instant now) {
        return new SellerSettlement(
                SellerSettlementId.forNew(),
                sellerId,
                null,
                SettlementCycle.MONTHLY,
                1,
                false,
                null,
                now,
                now);
    }

    /** DB에서 재구성. */
    public static SellerSettlement reconstitute(
            SellerSettlementId id,
            SellerId sellerId,
            BankAccount bankAccount,
            SettlementCycle settlementCycle,
            Integer settlementDay,
            boolean verified,
            Instant verifiedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new SellerSettlement(
                id,
                sellerId,
                bankAccount,
                settlementCycle,
                settlementDay,
                verified,
                verifiedAt,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /** SellerId 설정. */
    public void assignSellerId(SellerId sellerId) {
        this.sellerId = sellerId;
    }

    /** 계좌 정보 변경 (인증 초기화). */
    public void updateBankAccount(BankAccount bankAccount, Instant now) {
        this.bankAccount = bankAccount;
        this.verified = false;
        this.verifiedAt = null;
        this.updatedAt = now;
    }

    /** 정산 주기 변경. */
    public void updateSettlementCycle(SettlementCycle cycle, Integer day, Instant now) {
        this.settlementCycle = cycle;
        this.settlementDay = day;
        this.updatedAt = now;
    }

    /** 전체 정산 정보 업데이트. */
    public void update(SellerSettlementUpdateData data, Instant now) {
        BankAccount newBankAccount = data.bankAccount();
        if (newBankAccount != null && !newBankAccount.equals(this.bankAccount)) {
            this.bankAccount = newBankAccount;
            this.verified = false;
            this.verifiedAt = null;
        }
        this.settlementCycle = data.settlementCycle();
        this.settlementDay = data.settlementDay();
        this.updatedAt = now;
    }

    /** 계좌 인증 완료 처리. */
    public void verify(Instant now) {
        this.verified = true;
        this.verifiedAt = now;
        this.updatedAt = now;
    }

    /** 계좌 인증 취소. */
    public void unverify(Instant now) {
        this.verified = false;
        this.verifiedAt = null;
        this.updatedAt = now;
    }

    // Getters
    public SellerSettlementId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId != null ? sellerId.value() : null;
    }

    public BankAccount bankAccount() {
        return bankAccount;
    }

    public boolean hasBankAccount() {
        return bankAccount != null;
    }

    public String bankCode() {
        return bankAccount != null ? bankAccount.bankCode() : null;
    }

    public String bankName() {
        return bankAccount != null ? bankAccount.bankName() : null;
    }

    public String accountNumber() {
        return bankAccount != null ? bankAccount.accountNumber() : null;
    }

    public String maskedAccountNumber() {
        return bankAccount != null ? bankAccount.maskedAccountNumber() : null;
    }

    public String accountHolderName() {
        return bankAccount != null ? bankAccount.accountHolderName() : null;
    }

    public SettlementCycle settlementCycle() {
        return settlementCycle;
    }

    public Integer settlementDay() {
        return settlementDay;
    }

    public boolean isVerified() {
        return verified;
    }

    public Instant verifiedAt() {
        return verifiedAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
