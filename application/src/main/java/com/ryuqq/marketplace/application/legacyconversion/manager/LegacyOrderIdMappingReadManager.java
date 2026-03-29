package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderIdMappingQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 주문 ID 매핑 조회 Manager. */
@Component
@Transactional(readOnly = true)
public class LegacyOrderIdMappingReadManager {

    private final LegacyOrderIdMappingQueryPort queryPort;

    public LegacyOrderIdMappingReadManager(LegacyOrderIdMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * legacyOrderId로 매핑 조회.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 매핑 Optional
     */
    public Optional<LegacyOrderIdMapping> findByLegacyOrderId(long legacyOrderId) {
        return queryPort.findByLegacyOrderId(legacyOrderId);
    }

    /**
     * legacyOrderId에 해당하는 매핑 존재 여부 확인.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 존재 여부
     */
    public boolean existsByLegacyOrderId(long legacyOrderId) {
        return queryPort.existsByLegacyOrderId(legacyOrderId);
    }

    /**
     * market orderItemId 목록으로 매핑 배치 조회.
     *
     * @param orderItemIds market 주문 아이템 ID 목록
     * @return 매핑 목록
     */
    public List<LegacyOrderIdMapping> findByInternalOrderItemIds(List<Long> orderItemIds) {
        return queryPort.findByInternalOrderItemIds(orderItemIds);
    }
}
