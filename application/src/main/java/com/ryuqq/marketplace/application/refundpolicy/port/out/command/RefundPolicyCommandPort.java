package com.ryuqq.marketplace.application.refundpolicy.port.out.command;

import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import java.util.List;

/** 환불 정책 Command Port. */
public interface RefundPolicyCommandPort {

    Long persist(RefundPolicy refundPolicy);

    void persistAll(List<RefundPolicy> refundPolicies);
}
