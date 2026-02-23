package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductUpdateData;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class InboundProductCommandFactory {

    private final TimeProvider timeProvider;

    public InboundProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public InboundProduct create(ReceiveInboundProductCommand command) {
        Instant now = timeProvider.now();
        return InboundProduct.forNew(
                command.inboundSourceId(),
                ExternalProductCode.of(command.externalProductCode()),
                command.productName(),
                command.externalBrandCode(),
                command.externalCategoryCode(),
                command.sellerId(),
                command.regularPrice(),
                command.currentPrice(),
                command.optionType(),
                command.descriptionHtml(),
                command.rawPayloadJson(),
                now);
    }

    public InboundProductUpdateData toUpdateData(ReceiveInboundProductCommand command) {
        return InboundProductUpdateData.of(
                command.productName(),
                command.externalBrandCode(),
                command.externalCategoryCode(),
                command.regularPrice(),
                command.currentPrice(),
                command.optionType(),
                command.descriptionHtml(),
                command.rawPayloadJson());
    }
}
