package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.time.Instant;
import java.time.LocalTime;

/**
 * SellerCompositeDto - 셀러 Composite 조회 DTO.
 *
 * <p>Seller + SellerBusinessInfo + SellerCs 조인 결과.
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 조회합니다.
 *
 * <p>GetSellerForCustomerService, SearchSellerByOffsetService에서 사용.
 */
public record SellerCompositeDto(
        // Seller
        Long sellerId,
        String sellerName,
        String displayName,
        String logoUrl,
        String description,
        boolean active,
        Instant sellerCreatedAt,
        Instant sellerUpdatedAt,

        // SellerBusinessInfo
        Long businessInfoId,
        String registrationNumber,
        String companyName,
        String representative,
        String saleReportNumber,
        String businessZipcode,
        String businessAddress,
        String businessAddressDetail,

        // SellerCs
        Long csId,
        String csPhone,
        String csMobile,
        String csEmail,
        LocalTime operatingStartTime,
        LocalTime operatingEndTime,
        String operatingDays,
        String kakaoChannelUrl) {}
