package com.ryuqq.marketplace.application.refundpolicy.manager;

import com.ryuqq.marketplace.application.refundpolicy.port.out.command.RefundPolicyCommandPort;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 환불 정책 Command Manager. */
@Component
public class RefundPolicyCommandManager {

    private final RefundPolicyCommandPort commandPort;

    public RefundPolicyCommandManager(RefundPolicyCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(RefundPolicy refundPolicy) {
        return commandPort.persist(refundPolicy);
    }

    @Transactional
    public void persistAll(List<RefundPolicy> refundPolicies) {
        commandPort.persistAll(refundPolicies);
    }
}
