package com.ryuqq.marketplace.application.claimhistory.port.out.query;

import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.util.List;

/** 클레임 이력 Query Port. */
public interface ClaimHistoryQueryPort {

    List<ClaimHistory> findByClaimTypeAndClaimId(ClaimType claimType, String claimId);

    List<ClaimHistory> findByClaimTypeAndClaimIds(ClaimType claimType, List<String> claimIds);

    List<ClaimHistory> findByOrderItemId(Long orderItemId);

    List<ClaimHistory> findByCriteria(ClaimHistoryPageCriteria criteria);

    long countByCriteria(ClaimHistoryPageCriteria criteria);
}
