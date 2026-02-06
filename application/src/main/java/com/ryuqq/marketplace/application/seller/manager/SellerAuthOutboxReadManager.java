package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerAuthOutbox Read Manager.
 *
 * <p>Outbox 조회를 위한 매니저입니다.
 */
@Component
public class SellerAuthOutboxReadManager {

    private final SellerAuthOutboxQueryPort queryPort;

    public SellerAuthOutboxReadManager(SellerAuthOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Optional<SellerAuthOutbox> findPendingBySellerId(SellerId sellerId) {
        return queryPort.findPendingBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public List<SellerAuthOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxesForRetry(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<SellerAuthOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }
}
