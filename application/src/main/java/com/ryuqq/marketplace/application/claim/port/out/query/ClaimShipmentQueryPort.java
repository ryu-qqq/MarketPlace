package com.ryuqq.marketplace.application.claim.port.out.query;

import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import java.util.Optional;

/** 클레임 배송 Query Port. */
public interface ClaimShipmentQueryPort {

    /**
     * ID로 클레임 배송 정보를 조회합니다.
     *
     * @param id 클레임 배송 ID
     * @return 클레임 배송 (없으면 empty)
     */
    Optional<ClaimShipment> findById(ClaimShipmentId id);
}
