package com.ryuqq.marketplace.application.selleradmin.manager;

import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerAdminAuthOutbox Read Manager.
 *
 * <p>셀러 관리자 인증 Outbox 조회를 위한 매니저입니다.
 */
@Component
public class SellerAdminAuthOutboxReadManager {

    private final SellerAdminAuthOutboxQueryPort queryPort;

    public SellerAdminAuthOutboxReadManager(SellerAdminAuthOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SellerAdminAuthOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }

    @Transactional(readOnly = true)
    public Optional<SellerAdminAuthOutbox> findPendingBySellerAdminId(SellerAdminId sellerAdminId) {
        return queryPort.findPendingBySellerAdminId(sellerAdminId);
    }

    @Transactional(readOnly = true)
    public List<SellerAdminAuthOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxesForRetry(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<SellerAdminAuthOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }
}
