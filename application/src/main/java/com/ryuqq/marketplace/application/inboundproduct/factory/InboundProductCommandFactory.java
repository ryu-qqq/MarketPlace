package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** ReceiveInboundProductCommand → InboundProduct 매핑 레코드 생성 팩토리. */
@Component
public class InboundProductCommandFactory {

    private final TimeProvider timeProvider;

    public InboundProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** Command에서 매핑 필드만 추출하여 InboundProduct 매핑 레코드를 생성한다. */
    public InboundProduct create(ReceiveInboundProductCommand command) {
        Instant now = timeProvider.now();
        return InboundProduct.forNew(
                command.inboundSourceId(),
                ExternalProductCode.of(command.externalProductCode()),
                command.externalBrandCode(),
                command.externalCategoryCode(),
                command.sellerId(),
                now);
    }
}
