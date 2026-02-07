package com.ryuqq.marketplace.application.seller.dto.response;

import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;

/**
 * 고객용 셀러 조회 결과.
 *
 * <p>고객에게 노출되는 최소 정보만 포함
 *
 * @param id 셀러 ID
 * @param displayName 표시명
 * @param logoUrl 로고 URL
 * @param description 설명
 * @param companyName 회사명
 * @param representative 대표자명
 * @param csPhone CS 전화번호
 * @param csEmail CS 이메일
 */
public record SellerCustomerResult(
        Long id,
        String displayName,
        String logoUrl,
        String description,
        String companyName,
        String representative,
        String csPhone,
        String csEmail) {

    public static SellerCustomerResult of(
            Seller seller, SellerBusinessInfo businessInfo, SellerCs sellerCs) {
        return new SellerCustomerResult(
                seller.idValue(),
                seller.displayNameValue(),
                seller.logoUrlValue(),
                seller.descriptionValue(),
                businessInfo != null ? businessInfo.companyNameValue() : null,
                businessInfo != null ? businessInfo.representativeValue() : null,
                sellerCs != null ? sellerCs.csPhone() : null,
                sellerCs != null ? sellerCs.csEmail() : null);
    }
}
