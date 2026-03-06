package com.ryuqq.marketplace.application.refundpolicy.internal;

import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxCommandManager;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyCommandManager;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerEntityType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RefundPolicyOutboundFacade {

    private final RefundPolicyCommandManager commandManager;
    private final OutboundSellerOutboxCommandManager outboxManager;

    public RefundPolicyOutboundFacade(
            RefundPolicyCommandManager commandManager,
            OutboundSellerOutboxCommandManager outboxManager) {
        this.commandManager = commandManager;
        this.outboxManager = outboxManager;
    }

    public Long persistWithSync(RefundPolicy policy, OutboundSellerOperationType op, Instant now) {
        Long id = commandManager.persist(policy);
        outboxManager.persist(
                OutboundSellerOutbox.forNew(
                        policy.sellerId(), id, OutboundSellerEntityType.REFUND_POLICY, op, now));
        return id;
    }

    public void persistAllWithSync(
            SellerId sellerId,
            List<RefundPolicy> policies,
            OutboundSellerOperationType op,
            Instant now) {
        commandManager.persistAll(policies);
        for (RefundPolicy p : policies) {
            outboxManager.persist(
                    OutboundSellerOutbox.forNew(
                            sellerId,
                            p.idValue(),
                            OutboundSellerEntityType.REFUND_POLICY,
                            op,
                            now));
        }
    }
}
