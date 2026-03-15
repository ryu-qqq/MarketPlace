package com.ryuqq.marketplace.application.refund.port.out.query;

import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.util.List;
import java.util.Optional;

/** 환불 클레임 Query Port. */
public interface RefundQueryPort {

    Optional<RefundClaim> findById(RefundClaimId id);

    Optional<RefundClaim> findByOrderId(String orderId);

    List<RefundClaim> findByOrderIds(List<String> orderIds);
}
