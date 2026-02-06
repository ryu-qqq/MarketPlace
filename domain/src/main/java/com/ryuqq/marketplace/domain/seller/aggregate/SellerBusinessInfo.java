package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.seller.id.SellerBusinessInfoId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import java.time.Instant;

/** 셀러 사업자 정보 Aggregate. */
public class SellerBusinessInfo {

    private final SellerBusinessInfoId id;
    private SellerId sellerId;
    private RegistrationNumber registrationNumber;
    private CompanyName companyName;
    private Representative representative;
    private SaleReportNumber saleReportNumber;
    private Address businessAddress;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private SellerBusinessInfo(
            SellerBusinessInfoId id,
            SellerId sellerId,
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.registrationNumber = registrationNumber;
        this.companyName = companyName;
        this.representative = representative;
        this.saleReportNumber = saleReportNumber;
        this.businessAddress = businessAddress;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * SellerId 없이 새 SellerBusinessInfo 생성.
     *
     * <p>SellerId는 나중에 assignSellerId()로 설정합니다.
     */
    public static SellerBusinessInfo forNew(
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            Instant now) {
        return new SellerBusinessInfo(
                SellerBusinessInfoId.forNew(),
                null,
                registrationNumber,
                companyName,
                representative,
                saleReportNumber,
                businessAddress,
                DeletionStatus.active(),
                now,
                now);
    }

    public static SellerBusinessInfo forNew(
            SellerId sellerId,
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            Instant now) {
        return new SellerBusinessInfo(
                SellerBusinessInfoId.forNew(),
                sellerId,
                registrationNumber,
                companyName,
                representative,
                saleReportNumber,
                businessAddress,
                DeletionStatus.active(),
                now,
                now);
    }

    public static SellerBusinessInfo reconstitute(
            SellerBusinessInfoId id,
            SellerId sellerId,
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus status =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new SellerBusinessInfo(
                id,
                sellerId,
                registrationNumber,
                companyName,
                representative,
                saleReportNumber,
                businessAddress,
                status,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * SellerId를 설정합니다.
     *
     * @param sellerId 셀러 ID
     */
    public void assignSellerId(SellerId sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * 셀러 사업자 정보 수정.
     *
     * @param updateData 수정 데이터
     * @param now 현재 시간
     */
    public void update(SellerBusinessInfoUpdateData updateData, Instant now) {
        this.registrationNumber = updateData.registrationNumber();
        this.companyName = updateData.companyName();
        this.representative = updateData.representative();
        this.saleReportNumber = updateData.saleReportNumber();
        this.businessAddress = updateData.businessAddress();
        this.updatedAt = now;
    }

    /**
     * 셀러 사업자 정보 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 셀러 사업자 정보 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    // VO Getters
    public SellerBusinessInfoId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
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

    public String businessAddressZipCode() {
        return businessAddress != null ? businessAddress.zipcode() : null;
    }

    public String businessAddressRoad() {
        return businessAddress != null ? businessAddress.line1() : null;
    }

    public String businessAddressDetail() {
        return businessAddress != null ? businessAddress.line2() : null;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    /** 삭제 여부 확인 */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    /**
     * 삭제 시각 반환.
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}
