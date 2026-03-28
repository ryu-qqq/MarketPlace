package com.ryuqq.marketplace.application.claimhistory.manager;

import com.ryuqq.marketplace.application.claimhistory.port.out.query.ClaimHistoryQueryPort;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 클레임 이력 Read Manager. */
@Component
public class ClaimHistoryReadManager {

    private final ClaimHistoryQueryPort queryPort;

    public ClaimHistoryReadManager(ClaimHistoryQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ClaimHistory> findByClaimId(ClaimType claimType, String claimId) {
        return queryPort.findByClaimTypeAndClaimId(claimType, claimId);
    }

    @Transactional(readOnly = true)
    public List<ClaimHistory> findByClaimIds(ClaimType claimType, List<String> claimIds) {
        return queryPort.findByClaimTypeAndClaimIds(claimType, claimIds);
    }

    @Transactional(readOnly = true)
    public List<ClaimHistory> findByOrderItemId(Long orderItemId) {
        return queryPort.findByOrderItemId(orderItemId);
    }

    @Transactional(readOnly = true)
    public List<ClaimHistory> findByCriteria(ClaimHistoryPageCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ClaimHistoryPageCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
