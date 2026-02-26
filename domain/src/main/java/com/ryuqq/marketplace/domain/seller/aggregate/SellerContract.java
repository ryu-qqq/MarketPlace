package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.id.SellerContractId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CommissionRate;
import com.ryuqq.marketplace.domain.seller.vo.ContractStatus;
import java.time.Instant;
import java.time.LocalDate;

/**
 * 셀러 계약 정보 Entity.
 *
 * <p>Seller와 1:1 관계. 수수료율, 계약기간 등을 관리합니다.
 */
public class SellerContract {

    private final SellerContractId id;
    private SellerId sellerId;
    private CommissionRate commissionRate;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private ContractStatus status;
    private String specialTerms;
    private final Instant createdAt;
    private Instant updatedAt;

    private SellerContract(
            SellerContractId id,
            SellerId sellerId,
            CommissionRate commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            ContractStatus status,
            String specialTerms,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.commissionRate = commissionRate;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.status = status;
        this.specialTerms = specialTerms;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** SellerId 없이 새 SellerContract 생성. */
    public static SellerContract forNew(
            CommissionRate commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            String specialTerms,
            Instant now) {
        return new SellerContract(
                SellerContractId.forNew(),
                null,
                commissionRate,
                contractStartDate,
                contractEndDate,
                ContractStatus.ACTIVE,
                specialTerms,
                now,
                now);
    }

    /** SellerId와 함께 새 SellerContract 생성. */
    public static SellerContract forNew(
            SellerId sellerId,
            CommissionRate commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            String specialTerms,
            Instant now) {
        return new SellerContract(
                SellerContractId.forNew(),
                sellerId,
                commissionRate,
                contractStartDate,
                contractEndDate,
                ContractStatus.ACTIVE,
                specialTerms,
                now,
                now);
    }

    /** 기본 계약 생성 (수수료율만 지정, 기간 무제한). */
    public static SellerContract defaultContract(
            SellerId sellerId, CommissionRate commissionRate, Instant now) {
        return new SellerContract(
                SellerContractId.forNew(),
                sellerId,
                commissionRate,
                LocalDate.now(),
                null,
                ContractStatus.ACTIVE,
                null,
                now,
                now);
    }

    /** DB에서 재구성. */
    public static SellerContract reconstitute(
            SellerContractId id,
            SellerId sellerId,
            CommissionRate commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            ContractStatus status,
            String specialTerms,
            Instant createdAt,
            Instant updatedAt) {
        return new SellerContract(
                id,
                sellerId,
                commissionRate,
                contractStartDate,
                contractEndDate,
                status,
                specialTerms,
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

    /** 수수료율 변경. */
    public void updateCommissionRate(CommissionRate commissionRate, Instant now) {
        this.commissionRate = commissionRate;
        this.updatedAt = now;
    }

    /** 계약 기간 변경. */
    public void updateContractPeriod(LocalDate startDate, LocalDate endDate, Instant now) {
        this.contractStartDate = startDate;
        this.contractEndDate = endDate;
        this.updatedAt = now;
    }

    /** 특약사항 변경. */
    public void updateSpecialTerms(String specialTerms, Instant now) {
        this.specialTerms = specialTerms;
        this.updatedAt = now;
    }

    /** 전체 계약 정보 업데이트. */
    public void update(SellerContractUpdateData data, Instant now) {
        this.commissionRate = data.commissionRate();
        this.contractStartDate = data.contractStartDate();
        this.contractEndDate = data.contractEndDate();
        this.specialTerms = data.specialTerms();
        this.updatedAt = now;
    }

    /** 계약 해지. */
    public void terminate(Instant now) {
        this.status = ContractStatus.TERMINATED;
        this.updatedAt = now;
    }

    /** 계약 만료 처리. */
    public void expire(Instant now) {
        this.status = ContractStatus.EXPIRED;
        this.updatedAt = now;
    }

    /** 계약 유효 여부 확인. */
    public boolean isValid(LocalDate checkDate) {
        if (!status.isActive()) {
            return false;
        }
        if (contractEndDate != null && checkDate.isAfter(contractEndDate)) {
            return false;
        }
        return !checkDate.isBefore(contractStartDate);
    }

    // Getters
    public SellerContractId id() {
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

    public CommissionRate commissionRate() {
        return commissionRate;
    }

    public double commissionRateValue() {
        return commissionRate.doubleValue();
    }

    public LocalDate contractStartDate() {
        return contractStartDate;
    }

    public LocalDate contractEndDate() {
        return contractEndDate;
    }

    public ContractStatus status() {
        return status;
    }

    public String specialTerms() {
        return specialTerms;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status.isActive();
    }
}
