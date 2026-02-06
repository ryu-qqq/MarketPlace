package com.ryuqq.marketplace.application.shippingpolicy.port.out.command;

import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.List;

/** 배송 정책 Command Port. */
public interface ShippingPolicyCommandPort {

    Long persist(ShippingPolicy shippingPolicy);

    void persistAll(List<ShippingPolicy> shippingPolicies);
}
