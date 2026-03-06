package com.ryuqq.marketplace.application.shippingpolicy.internal;

import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerEntityType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ShippingPolicyOutboundFacade {

    private final ShippingPolicyCommandManager commandManager;
    private final OutboundSellerOutboxCommandManager outboxManager;

    public ShippingPolicyOutboundFacade(
            ShippingPolicyCommandManager commandManager,
            OutboundSellerOutboxCommandManager outboxManager) {
        this.commandManager = commandManager;
        this.outboxManager = outboxManager;
    }

    public Long persistWithSync(
            ShippingPolicy policy, OutboundSellerOperationType op, Instant now) {
        Long id = commandManager.persist(policy);
        outboxManager.persist(
                OutboundSellerOutbox.forNew(
                        policy.sellerId(), id, OutboundSellerEntityType.SHIPPING_POLICY, op, now));
        return id;
    }

    public void persistAllWithSync(
            SellerId sellerId,
            List<ShippingPolicy> policies,
            OutboundSellerOperationType op,
            Instant now) {
        commandManager.persistAll(policies);
        for (ShippingPolicy p : policies) {
            outboxManager.persist(
                    OutboundSellerOutbox.forNew(
                            sellerId,
                            p.idValue(),
                            OutboundSellerEntityType.SHIPPING_POLICY,
                            op,
                            now));
        }
    }
}
