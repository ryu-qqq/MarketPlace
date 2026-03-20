package com.ryuqq.marketplace.application.refund.manager;

import com.ryuqq.marketplace.application.refund.port.out.query.RefundQueryPort;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.exception.RefundNotFoundException;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** RefundClaim Read Manager. */
@Component
public class RefundReadManager {

    private final RefundQueryPort queryPort;

    public RefundReadManager(RefundQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public RefundClaim getById(RefundClaimId id) {
        return queryPort.findById(id).orElseThrow(() -> new RefundNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<RefundClaim> findByIdIn(List<String> refundClaimIds, Long sellerId) {
        return queryPort.findByIdIn(refundClaimIds, sellerId);
    }

    @Transactional(readOnly = true)
    public List<RefundClaim> findByCriteria(RefundSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(RefundSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public Optional<RefundClaim> findByOrderItemId(String orderItemId) {
        return queryPort.findByOrderItemId(orderItemId);
    }

    @Transactional(readOnly = true)
    public Map<RefundStatus, Long> countByStatus() {
        return queryPort.countByStatus();
    }
}
