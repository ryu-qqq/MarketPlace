package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;
import java.util.Optional;

/** 레거시 주문 ID 매핑 조회 포트. */
public interface LegacyOrderIdMappingQueryPort {

    /**
     * legacyOrderId로 매핑 조회.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 매핑 Optional
     */
    Optional<LegacyOrderIdMapping> findByLegacyOrderId(long legacyOrderId);

    /**
     * legacyOrderId에 해당하는 매핑 존재 여부 확인.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 존재 여부
     */
    boolean existsByLegacyOrderId(long legacyOrderId);

    /**
     * market orderItemId 목록으로 매핑 배치 조회.
     *
     * @param orderItemIds market 주문 아이템 ID 목록
     * @return 매핑 목록
     */
    List<LegacyOrderIdMapping> findByInternalOrderItemIds(List<Long> orderItemIds);
}
