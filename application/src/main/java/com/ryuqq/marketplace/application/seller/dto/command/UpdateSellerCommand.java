package com.ryuqq.marketplace.application.seller.dto.command;

/**
 * 셀러 정보 수정 Command.
 *
 * <p>Seller 기본정보 + CS + BusinessInfo를 한번에 수정합니다.
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 수정합니다.
 *
 * @param sellerId 셀러 ID
 * @param sellerName 셀러명
 * @param displayName 표시명
 * @param logoUrl 로고 URL
 * @param description 설명
 * @param csInfo CS 정보 (nullable)
 * @param businessInfo 사업자 정보 (nullable)
 */
public record UpdateSellerCommand(
        Long sellerId,
        String sellerName,
        String displayName,
        String logoUrl,
        String description,
        CsInfoCommand csInfo,
        BusinessInfoCommand businessInfo) {

    /** 기본 정보만 수정하는 생성자 (하위 호환). */
    public UpdateSellerCommand(
            Long sellerId,
            String sellerName,
            String displayName,
            String logoUrl,
            String description) {
        this(sellerId, sellerName, displayName, logoUrl, description, null, null);
    }

    /** CS 정보 Command. */
    public record CsInfoCommand(String phone, String email, String mobile) {}

    /** 주소 Command. */
    public record AddressCommand(String zipCode, String line1, String line2) {}

    /** 사업자 정보 Command. */
    public record BusinessInfoCommand(
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            AddressCommand businessAddress) {}
}
