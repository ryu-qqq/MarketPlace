package com.ryuqq.marketplace.application.seller.dto.command;

/**
 * 셀러 등록 Command.
 *
 * <p>Seller + BusinessInfo를 한번에 등록합니다. (모두 1:1 관계)
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 등록합니다.
 *
 * @param seller 셀러 기본 정보
 * @param businessInfo 사업자 정보
 */
public record RegisterSellerCommand(
        SellerInfoCommand seller, SellerBusinessInfoCommand businessInfo) {

    /** 셀러 기본 정보 Command. */
    public record SellerInfoCommand(
            String sellerName, String displayName, String logoUrl, String description) {}

    /** 사업자 정보 Command. */
    public record SellerBusinessInfoCommand(
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            AddressCommand businessAddress,
            CsContactCommand csContact) {}

    /** 공통 주소 Command. */
    public record AddressCommand(String zipCode, String line1, String line2) {}

    /** CS 연락처 Command. */
    public record CsContactCommand(String phone, String email, String mobile) {}
}
