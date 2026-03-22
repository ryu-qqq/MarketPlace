package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.LegacyDeliveryData;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.LegacyRefundData;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyCommandManager;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.vo.RefundPolicyName;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.LeadTime;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingFeeType;
import com.ryuqq.marketplace.domain.shippingpolicy.vo.ShippingPolicyName;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 셀러의 디폴트 배송정책/환불정책 리졸버.
 *
 * <p>디폴트 정책이 있으면 디폴트 ID 반환, 없으면 레거시 데이터로 새 정책 생성 후 ID 반환.
 */
@Component
public class LegacyDefaultPolicyResolver {

    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final ShippingPolicyCommandManager shippingPolicyCommandManager;
    private final RefundPolicyReadManager refundPolicyReadManager;
    private final RefundPolicyCommandManager refundPolicyCommandManager;

    public LegacyDefaultPolicyResolver(
            ShippingPolicyReadManager shippingPolicyReadManager,
            ShippingPolicyCommandManager shippingPolicyCommandManager,
            RefundPolicyReadManager refundPolicyReadManager,
            RefundPolicyCommandManager refundPolicyCommandManager) {
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.shippingPolicyCommandManager = shippingPolicyCommandManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
        this.refundPolicyCommandManager = refundPolicyCommandManager;
    }

    /**
     * 배송정책 리졸빙.
     *
     * <p>디폴트 정책이 있으면 디폴트 ID 반환, 없으면 레거시 데이터로 새로 생성.
     */
    public long resolveShippingPolicyId(long internalSellerId, LegacyDeliveryData deliveryData) {
        SellerId sellerId = SellerId.of(internalSellerId);
        Optional<ShippingPolicy> defaultPolicy =
                shippingPolicyReadManager.findDefaultBySellerId(sellerId);

        if (defaultPolicy.isPresent()) {
            return defaultPolicy.get().idValue();
        }

        Instant now = Instant.now();
        ShippingPolicy newPolicy = ShippingPolicy.forNew(
                sellerId,
                ShippingPolicyName.of("레거시 배송정책"),
                true,
                deliveryData.deliveryFee() == 0 ? ShippingFeeType.FREE : ShippingFeeType.PAID,
                Money.of((int) deliveryData.deliveryFee()),
                null,
                Money.zero(),
                Money.zero(),
                Money.zero(),
                Money.zero(),
                LeadTime.of(
                        deliveryData.deliveryPeriodAverage(),
                        deliveryData.deliveryPeriodAverage(),
                        LocalTime.of(14, 0)),
                now);

        return shippingPolicyCommandManager.persist(newPolicy);
    }

    /**
     * 환불정책 리졸빙.
     *
     * <p>디폴트 정책이 있으면 디폴트 ID 반환, 없으면 레거시 데이터로 새로 생성.
     */
    public long resolveRefundPolicyId(long internalSellerId, LegacyRefundData refundData) {
        SellerId sellerId = SellerId.of(internalSellerId);
        Optional<RefundPolicy> defaultPolicy =
                refundPolicyReadManager.findDefaultBySellerId(sellerId);

        if (defaultPolicy.isPresent()) {
            return defaultPolicy.get().idValue();
        }

        Instant now = Instant.now();
        RefundPolicy newPolicy = RefundPolicy.forNew(
                sellerId,
                RefundPolicyName.of("레거시 환불정책"),
                true,
                7,
                7,
                List.of(),
                false,
                false,
                0,
                refundData.returnExchangeAreaDomestic(),
                now);

        return refundPolicyCommandManager.persist(newPolicy);
    }
}
