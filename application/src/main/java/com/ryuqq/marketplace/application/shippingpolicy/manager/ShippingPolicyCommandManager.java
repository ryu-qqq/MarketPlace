package com.ryuqq.marketplace.application.shippingpolicy.manager;

import com.ryuqq.marketplace.application.shippingpolicy.port.out.command.ShippingPolicyCommandPort;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 배송 정책 Command Manager. */
@Component
public class ShippingPolicyCommandManager {

    private final ShippingPolicyCommandPort commandPort;

    public ShippingPolicyCommandManager(ShippingPolicyCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ShippingPolicy shippingPolicy) {
        return commandPort.persist(shippingPolicy);
    }

    @Transactional
    public void persistAll(List<ShippingPolicy> shippingPolicies) {
        commandPort.persistAll(shippingPolicies);
    }
}
