package com.ryuqq.marketplace.application.seller.dto.response;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;

/**
 * 셀러 CS 정보 동기화용 결과 DTO.
 *
 * <p>외부 채널 동기화 시 필요한 CS 연락처 정보만 포함합니다.
 *
 * @param csPhone CS 전화번호
 * @param csMobile CS 모바일번호
 */
public record SellerCsSyncResult(String csPhone, String csMobile) {

    public static SellerCsSyncResult from(SellerCs sellerCs) {
        return new SellerCsSyncResult(sellerCs.csPhone(), sellerCs.csMobile());
    }
}
