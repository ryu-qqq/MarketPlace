package com.ryuqq.marketplace.application.selleraddress.dto.command;

/**
 * 셀러 주소 삭제 Command.
 *
 * @param addressId 주소 ID
 */
public record DeleteSellerAddressCommand(Long addressId) {}
