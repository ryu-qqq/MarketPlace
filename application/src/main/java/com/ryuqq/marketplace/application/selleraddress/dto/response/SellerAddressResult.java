package com.ryuqq.marketplace.application.selleraddress.dto.response;

import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.time.Instant;

/**
 * 셀러 주소 조회 결과.
 *
 * @param id 주소 ID
 * @param sellerId 셀러 ID
 * @param addressType 주소 유형 (SHIPPING/RETURN)
 * @param addressName 주소명
 * @param address 주소
 * @param defaultAddress 기본 주소 여부
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
public record SellerAddressResult(
        Long id,
        Long sellerId,
        String addressType,
        String addressName,
        AddressResult address,
        boolean defaultAddress,
        Instant createdAt,
        Instant updatedAt) {

    public static SellerAddressResult from(SellerAddress addr) {
        return new SellerAddressResult(
                addr.idValue(),
                addr.sellerIdValue(),
                addr.addressType().name(),
                addr.addressNameValue(),
                new AddressResult(addr.addressZipCode(), addr.addressRoad(), addr.addressDetail()),
                addr.isDefaultAddress(),
                addr.createdAt(),
                addr.updatedAt());
    }

    /** 주소 결과. */
    public record AddressResult(String zipCode, String line1, String line2) {}
}
