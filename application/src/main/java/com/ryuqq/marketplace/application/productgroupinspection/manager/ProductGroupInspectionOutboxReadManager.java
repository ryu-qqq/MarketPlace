package com.ryuqq.marketplace.application.productgroupinspection.manager;

import com.ryuqq.marketplace.application.productgroupinspection.port.out.query.ProductGroupInspectionOutboxQueryPort;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 검수 Outbox Read Manager. */
@Component
public class ProductGroupInspectionOutboxReadManager {

    private final ProductGroupInspectionOutboxQueryPort queryPort;

    public ProductGroupInspectionOutboxReadManager(
            ProductGroupInspectionOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ProductGroupInspectionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxes(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<ProductGroupInspectionOutbox> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findInProgressTimeoutOutboxes(timeoutThreshold, limit);
    }

    @Transactional(readOnly = true)
    public Optional<ProductGroupInspectionOutbox> findById(Long outboxId) {
        return queryPort.findById(outboxId);
    }
}
