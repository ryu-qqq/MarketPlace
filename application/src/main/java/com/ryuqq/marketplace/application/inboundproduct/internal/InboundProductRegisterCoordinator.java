package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductCommandFactory;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InboundProductRegisterCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductRegisterCoordinator.class);

    private final InboundProductCommandFactory factory;
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;

    public InboundProductRegisterCoordinator(
            InboundProductCommandFactory factory,
            InboundProductCommandManager commandManager,
            InboundProductMappingResolver mappingResolver) {
        this.factory = factory;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
    }

    public InboundProductConversionResult register(ReceiveInboundProductCommand command) {
        InboundProduct newProduct = factory.create(command);
        Instant now = newProduct.createdAt();
        InboundProductMappingResult mapping =
                mappingResolver.resolveMappingAndApply(newProduct, now);

        if (mapping.isFullyMapped()) {
            newProduct.markPendingConversion(now);
        }

        commandManager.persist(newProduct);
        log.info(
                "인바운드 상품 신규 수신: inboundSourceId={}, code={}, status={}",
                command.inboundSourceId(),
                command.externalProductCode(),
                newProduct.status());

        if (newProduct.isPendingConversion()) {
            return InboundProductConversionResult.pendingConversion(newProduct.idValue());
        }
        return InboundProductConversionResult.pendingMapping(newProduct.idValue());
    }
}
