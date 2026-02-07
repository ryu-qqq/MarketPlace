package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * SellerAdminCompositeDto - 셀러 Admin Composite 조회 DTO.
 *
 * <p>Seller + SellerBusinessInfo + SellerCs + SellerContract + SellerSettlement 조인 결과.
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 조회합니다.
 *
 * <p>GetSellerForAdminService에서 사용.
 */
public record SellerAdminCompositeDto(
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
        String kakaoChannelUrl,

        // SellerContract
        Long contractId,
        BigDecimal commissionRate,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        String contractStatus,
        String specialTerms,
        Instant contractCreatedAt,
        Instant contractUpdatedAt,

        // SellerSettlement
        Long settlementId,
        String bankCode,
        String bankName,
        String accountNumber,
        String accountHolderName,
        String settlementCycle,
        Integer settlementDay,
        boolean verified,
        Instant verifiedAt,
        Instant settlementCreatedAt,
        Instant settlementUpdatedAt) {}
