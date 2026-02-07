package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;

/**
 * SellerCs 수정 데이터 Value Object.
 *
 * @param csContact CS 연락처
 * @param operatingHours 운영시간
 * @param operatingDays 운영요일
 * @param kakaoChannelUrl 카카오 채널 URL
 */
public record SellerCsUpdateData(
        CsContact csContact,
        OperatingHours operatingHours,
        String operatingDays,
        String kakaoChannelUrl) {

    public static SellerCsUpdateData of(
            CsContact csContact,
            OperatingHours operatingHours,
            String operatingDays,
            String kakaoChannelUrl) {
        return new SellerCsUpdateData(csContact, operatingHours, operatingDays, kakaoChannelUrl);
    }
}
