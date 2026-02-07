package com.ryuqq.marketplace.application.selleraddress.dto.command;

/**
 * 셀러 주소 등록 Command.
 *
 * @param sellerId 셀러 ID (필수)
 * @param addressType 주소 유형 (SHIPPING/RETURN)
 * @param addressName 주소명
 * @param address 주소
 * @param defaultAddress 기본 주소 여부
 */
public record RegisterSellerAddressCommand(
        Long sellerId,
        String addressType,
        String addressName,
        AddressCommand address,
        boolean defaultAddress) {

    /** 공통 주소 Command. */
    public record AddressCommand(String zipCode, String line1, String line2) {}
}
