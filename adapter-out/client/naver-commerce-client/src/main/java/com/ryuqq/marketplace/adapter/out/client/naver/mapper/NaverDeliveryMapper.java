package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo.ClaimDeliveryInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo.DeliveryFee;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo.DeliveryFeeByArea;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;

/** 배송 정보 변환 매퍼. */
final class NaverDeliveryMapper {

    private static final String DELIVERY_TYPE_DELIVERY = "DELIVERY";
    private static final String DELIVERY_ATTR_NORMAL = "NORMAL";
    private static final String DELIVERY_COMPANY_DEFAULT = "CJGLS";
    private static final String DELIVERY_FEE_PAY_TYPE_PREPAID = "PREPAID";
    private static final String AREA_TYPE_AREA2 = "AREA_2";

    private NaverDeliveryMapper() {}

    static DeliveryInfo mapDeliveryInfo(ShippingPolicyResult shipping) {
        String feeType = mapDeliveryFeeType(shipping.shippingFeeType(), shipping.baseFee());
        int baseFee = shipping.baseFee() != null ? shipping.baseFee().intValue() : 0;
        Long freeConditionalAmount =
                "CONDITIONAL_FREE".equals(feeType) ? shipping.freeThreshold() : null;
        DeliveryFee fee =
                new DeliveryFee(
                        feeType, DELIVERY_FEE_PAY_TYPE_PREPAID, baseFee, freeConditionalAmount);

        DeliveryFeeByArea feeByArea = null;
        if (shipping.jejuExtraFee() != null || shipping.islandExtraFee() != null) {
            feeByArea =
                    new DeliveryFeeByArea(
                            shipping.jejuExtraFee(), shipping.islandExtraFee(), AREA_TYPE_AREA2);
        }

        ClaimDeliveryInfo claimInfo = null;
        if (shipping.returnFee() != null || shipping.exchangeFee() != null) {
            claimInfo = new ClaimDeliveryInfo(shipping.returnFee(), shipping.exchangeFee());
        }

        return new DeliveryInfo(
                DELIVERY_TYPE_DELIVERY,
                DELIVERY_ATTR_NORMAL,
                fee,
                DELIVERY_COMPANY_DEFAULT,
                feeByArea,
                claimInfo);
    }

    private static String mapDeliveryFeeType(String shippingFeeType, Long baseFee) {
        if ("FREE".equals(shippingFeeType)) {
            return "FREE";
        }
        if ("CONDITIONAL_FREE".equals(shippingFeeType)) {
            return "CONDITIONAL_FREE";
        }
        if (baseFee != null && baseFee > 0) {
            return "PAID";
        }
        return "FREE";
    }
}
