package com.ryuqq.marketplace.application.seller.dto.command;

/**
 * 사업자 정보 수정 Command.
 *
 * @param sellerId 셀러 ID
 * @param registrationNumber 사업자등록번호
 * @param companyName 회사명
 * @param representative 대표자명
 * @param saleReportNumber 통신판매업 신고번호
 * @param businessAddress 사업장 주소
 * @param csContact CS 연락처
 */
public record UpdateSellerBusinessInfoCommand(
        Long sellerId,
        String registrationNumber,
        String companyName,
        String representative,
        String saleReportNumber,
        AddressCommand businessAddress,
        CsContactCommand csContact) {

    /** 공통 주소 Command. */
    public record AddressCommand(String zipCode, String line1, String line2) {}

    /** CS 연락처 Command. */
    public record CsContactCommand(String phone, String email, String mobile) {}
}
