package com.ryuqq.marketplace.application.selleraddress.dto.command;

/**
 * 셀러 주소 수정 Command.
 *
 * @param addressId 주소 ID
 * @param addressName 주소명
 * @param address 주소
 * @param defaultAddress 기본 주소로 설정 여부 (null이면 변경 없음, true면 기본으로 설정)
 */
public record UpdateSellerAddressCommand(
        Long addressId, String addressName, AddressCommand address, Boolean defaultAddress) {

    /** 공통 주소 Command. */
    public record AddressCommand(String zipCode, String line1, String line2) {}
}
