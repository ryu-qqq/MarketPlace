package com.ryuqq.marketplace.domain.selleraddress.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressName;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.time.Instant;

/** 셀러 주소 (출고지/반품지) Aggregate. */
public class SellerAddress {

    private final SellerAddressId id;
    private final SellerId sellerId;
    private final AddressType addressType;
    private AddressName addressName;
    private Address address;
    private boolean defaultAddress;
    private DeletionStatus deletionStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    private SellerAddress(
            SellerAddressId id,
            SellerId sellerId,
            AddressType addressType,
            AddressName addressName,
            Address address,
            boolean defaultAddress,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.addressType = addressType;
        this.addressName = addressName;
        this.address = address;
        this.defaultAddress = defaultAddress;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새 SellerAddress 생성. SellerId 필수.
     *
     * @param sellerId 셀러 ID (필수)
     * @param addressType 주소 유형
     * @param addressName 주소 별칭
     * @param address 주소
     * @param defaultAddress 기본 주소 여부
     * @param now 현재 시간
     * @return SellerAddress
     */
    public static SellerAddress forNew(
            SellerId sellerId,
            AddressType addressType,
            AddressName addressName,
            Address address,
            boolean defaultAddress,
            Instant now) {
        if (sellerId == null) {
            throw new IllegalArgumentException("SellerId는 필수입니다");
        }
        return new SellerAddress(
                SellerAddressId.forNew(),
                sellerId,
                addressType,
                addressName,
                address,
                defaultAddress,
                DeletionStatus.active(),
                now,
                now);
    }

    public static SellerAddress reconstitute(
            SellerAddressId id,
            SellerId sellerId,
            AddressType addressType,
            AddressName addressName,
            Address address,
            boolean defaultAddress,
            Instant deletedAt,
            Instant createdAt,
            Instant updatedAt) {
        DeletionStatus status =
                deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
        return new SellerAddress(
                id,
                sellerId,
                addressType,
                addressName,
                address,
                defaultAddress,
                status,
                createdAt,
                updatedAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 셀러 주소 정보 수정.
     *
     * @param updateData 수정 데이터
     * @param now 현재 시간
     */
    public void update(SellerAddressUpdateData updateData, Instant now) {
        this.addressName = updateData.addressName();
        this.address = updateData.address();
        this.updatedAt = now;
    }

    public void markAsDefault(Instant now) {
        this.defaultAddress = true;
        this.updatedAt = now;
    }

    public void unmarkDefault(Instant now) {
        this.defaultAddress = false;
        this.updatedAt = now;
    }

    /**
     * 셀러 주소 삭제 (Soft Delete).
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 셀러 주소 복원.
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    public boolean hasSameAddressType(SellerAddress other) {
        return this.addressType == other.addressType;
    }

    public boolean isShippingAddress() {
        return addressType == AddressType.SHIPPING;
    }

    public boolean isReturnAddress() {
        return addressType == AddressType.RETURN;
    }

    // VO Getters
    public SellerAddressId id() {
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

    public AddressType addressType() {
        return addressType;
    }

    public AddressName addressName() {
        return addressName;
    }

    public String addressNameValue() {
        return addressName.value();
    }

    public Address address() {
        return address;
    }

    public String addressZipCode() {
        return address != null ? address.zipcode() : null;
    }

    public String addressRoad() {
        return address != null ? address.line1() : null;
    }

    public String addressDetail() {
        return address != null ? address.line2() : null;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
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
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}
