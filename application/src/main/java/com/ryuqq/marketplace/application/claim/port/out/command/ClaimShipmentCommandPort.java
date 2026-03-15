package com.ryuqq.marketplace.application.claim.port.out.command;

import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;

/** 클레임 배송 Command Port. */
public interface ClaimShipmentCommandPort {

    /**
     * 클레임 배송 정보를 저장합니다.
     *
     * @param claimShipment 저장할 클레임 배송 Aggregate
     */
    void persist(ClaimShipment claimShipment);
}
