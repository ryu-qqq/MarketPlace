package com.ryuqq.marketplace.application.seller.dto.bundle;

import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.id.SellerId;

/**
 * 셀러 등록 번들.
 *
 * <p>Seller + BusinessInfo를 한번에 묶어서 관리합니다. (모두 1:1 관계)
 *
 * <p>Address는 독립 Aggregate로 분리되어 별도 API로 등록합니다.
 */
public class SellerRegistrationBundle {

    private final Seller seller;
    private final SellerBusinessInfo businessInfo;

    public SellerRegistrationBundle(Seller seller, SellerBusinessInfo businessInfo) {
        this.seller = seller;
        this.businessInfo = businessInfo;
    }

    /**
     * SellerId를 BusinessInfo에 설정합니다.
     *
     * @param sellerId persist 후 확정된 Seller ID
     */
    public void withSellerId(SellerId sellerId) {
        businessInfo.assignSellerId(sellerId);
    }

    // === 편의 메서드 ===

    public String sellerNameValue() {
        return seller.sellerNameValue();
    }

    public String registrationNumberValue() {
        return businessInfo.registrationNumberValue();
    }

    // === Getter ===

    public Seller seller() {
        return seller;
    }

    public SellerBusinessInfo businessInfo() {
        return businessInfo;
    }
}
