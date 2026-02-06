package com.ryuqq.marketplace.application.selleradmin.manager;

import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminEmailOutboxQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerAdminEmailOutbox Read Manager.
 *
 * <p>셀러 관리자 이메일 Outbox 조회를 위한 매니저입니다.
 */
@Component
public class SellerAdminEmailOutboxReadManager {

    private final SellerAdminEmailOutboxQueryPort queryPort;

    public SellerAdminEmailOutboxReadManager(SellerAdminEmailOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Optional<SellerAdminEmailOutbox> findPendingBySellerId(SellerId sellerId) {
        return queryPort.findPendingBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public List<SellerAdminEmailOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxesForRetry(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<SellerAdminEmailOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }
}
