package com.ryuqq.marketplace.application.seller.dto.composite;

import java.time.Instant;

/**
 * SellerCompositeResult - 셀러 Composite 조회 결과.
 *
 * <p>Seller + SellerBusinessInfo + SellerCs 조인 결과.
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 조회합니다.
 */
public record SellerCompositeResult(SellerInfo seller, BusinessInfo businessInfo, CsInfo csInfo) {

    public record SellerInfo(
            Long id,
            String sellerName,
            String displayName,
            String logoUrl,
            String description,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {}

    public record BusinessInfo(
            Long id,
            String registrationNumber,
            String companyName,
            String representative,
            String saleReportNumber,
            String businessZipcode,
            String businessAddress,
            String businessAddressDetail) {}

    public record CsInfo(
            Long id,
            String csPhone,
            String csMobile,
            String csEmail,
            String operatingStartTime,
            String operatingEndTime,
            String operatingDays,
            String kakaoChannelUrl) {}
}
