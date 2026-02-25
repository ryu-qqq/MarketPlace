package com.ryuqq.marketplace.application.seller.dto.response;

/**
 * SellerPublicProfileResult - 셀러 공개 프로필 조회 결과.
 *
 * <p>셀러명, 표시명, 회사명, 대표자명만 포함하는 간소화된 공개 프로필.
 *
 * @param sellerName 셀러명
 * @param displayName 표시명
 * @param companyName 회사명
 * @param representative 대표자명
 */
public record SellerPublicProfileResult(
        String sellerName, String displayName, String companyName, String representative) {}
