package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductRegisterCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductUpdateCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ReceiveInboundProductService implements ReceiveInboundProductUseCase {

    private final InboundProductReadManager readManager;
    private final InboundProductRegisterCoordinator registerCoordinator;
    private final InboundProductUpdateCoordinator updateCoordinator;

    public ReceiveInboundProductService(
            InboundProductReadManager readManager,
            InboundProductRegisterCoordinator registerCoordinator,
            InboundProductUpdateCoordinator updateCoordinator) {
        this.readManager = readManager;
        this.registerCoordinator = registerCoordinator;
        this.updateCoordinator = updateCoordinator;
    }

    @Override
    public InboundProductConversionResult execute(ReceiveInboundProductCommand command) {
        Optional<InboundProduct> existing =
                readManager.findByInboundSourceIdAndProductCode(
                        command.inboundSourceId(), command.externalProductCode());

        if (existing.isPresent()) {
            return updateCoordinator.update(existing.get(), command);
        }
        return registerCoordinator.register(command);
    }
}
