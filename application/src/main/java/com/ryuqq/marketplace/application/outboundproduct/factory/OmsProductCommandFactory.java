package com.ryuqq.marketplace.application.outboundproduct.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import org.springframework.stereotype.Component;

/** OMS 상품 커맨드 Factory. StatusChangeContext 생성을 담당한다. */
@Component
public class OmsProductCommandFactory {

    private final TimeProvider timeProvider;

    public OmsProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public StatusChangeContext<Long> createRetryContext(long outboxId) {
        return new StatusChangeContext<>(outboxId, timeProvider.now());
    }
}
