package com.ryuqq.marketplace.application.refund.port.out.query;

import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 환불 클레임 Query Port. */
public interface RefundQueryPort {

    Optional<RefundClaim> findById(RefundClaimId id);

    Optional<RefundClaim> findByOrderItemId(Long orderItemId);

    List<RefundClaim> findByOrderItemIds(List<Long> orderItemIds);

    List<RefundClaim> findByCriteria(RefundSearchCriteria criteria);

    long countByCriteria(RefundSearchCriteria criteria);

    Map<RefundStatus, Long> countByStatus();

    List<RefundClaim> findAllByOrderItemId(Long orderItemId);

    /** refundClaimId 목록으로 일괄 조회. sellerId가 null이면 전체 조회 (슈퍼어드민). */
    List<RefundClaim> findByIdIn(List<String> refundClaimIds, Long sellerId);
}
