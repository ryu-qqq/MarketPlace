package com.ryuqq.marketplace.application.outboundproduct.service.command;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import com.ryuqq.marketplace.application.outboundproduct.internal.ManualSyncProductsCoordinator;
import com.ryuqq.marketplace.application.outboundproduct.port.in.command.ManualSyncProductsUseCase;
import org.springframework.stereotype.Service;

/** 수동 상품 외부몰 전송 Service. */
@Service
public class ManualSyncProductsService implements ManualSyncProductsUseCase {

    private final ManualSyncProductsCoordinator coordinator;

    public ManualSyncProductsService(ManualSyncProductsCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public ManualSyncResult execute(ManualSyncProductsCommand command) {
        return coordinator.execute(command);
    }
}
