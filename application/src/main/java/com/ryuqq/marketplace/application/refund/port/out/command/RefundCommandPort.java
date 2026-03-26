package com.ryuqq.marketplace.application.refund.port.out.command;

import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;

/** 환불 클레임 Command Port. */
public interface RefundCommandPort {

    void persist(RefundClaim refundClaim);
}
