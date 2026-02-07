package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.DisplayName;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;

/**
 * 셀러 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 *
 * <p>VO 객체들은 이미 null 검증이 완료된 객체이므로, UpdateData에서는 추가 검증을 하지 않습니다.
 */
public record SellerUpdateData(
        SellerName sellerName, DisplayName displayName, LogoUrl logoUrl, Description description) {

    public static SellerUpdateData of(
            SellerName sellerName,
            DisplayName displayName,
            LogoUrl logoUrl,
            Description description) {
        return new SellerUpdateData(sellerName, displayName, logoUrl, description);
    }

    public static SellerUpdateData of(
            String sellerName, String displayName, String logoUrl, String description) {
        return new SellerUpdateData(
                SellerName.of(sellerName),
                DisplayName.of(displayName),
                logoUrl != null ? LogoUrl.of(logoUrl) : null,
                description != null ? Description.of(description) : null);
    }
}
