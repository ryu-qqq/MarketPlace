package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.port.out.command.ProductGroupInspectionOutboxCommandPort;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 검수 Outbox Command Manager. */
@Component
public class ProductGroupInspectionOutboxCommandManager {

    private final ProductGroupInspectionOutboxCommandPort commandPort;

    public ProductGroupInspectionOutboxCommandManager(
            ProductGroupInspectionOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductGroupInspectionOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
