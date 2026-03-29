package com.ryuqq.marketplace.application.legacy.order.resolver;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 ID ↔ Market 주문 ID 양방향 리졸버.
 *
 * <p>legacy_order_id_mappings 테이블에서 직접 조회합니다. legacyOrderId → internalOrderId(UUID) +
 * internalOrderItemId(Long) 변환.
 */
@Component
public class LegacyOrderIdResolver {

    private final LegacyOrderIdMappingReadManager mappingReadManager;

    public LegacyOrderIdResolver(LegacyOrderIdMappingReadManager mappingReadManager) {
        this.mappingReadManager = mappingReadManager;
    }

    /**
     * 레거시 orderId → market 매핑 전체 조회.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 매핑 Optional
     */
    public Optional<LegacyOrderIdMapping> resolve(long legacyOrderId) {
        return mappingReadManager.findByLegacyOrderId(legacyOrderId);
    }

    /**
     * 레거시 orderId → market orderId(UUID) 변환.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return market orderId(UUID), 매핑 없으면 empty
     */
    public Optional<String> resolveOrderId(long legacyOrderId) {
        return resolve(legacyOrderId).map(LegacyOrderIdMapping::internalOrderId);
    }

    /**
     * 레거시 orderId → market orderItemId(Long) 변환.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return market orderItemId, 매핑 없으면 empty
     */
    public Optional<Long> resolveOrderItemId(long legacyOrderId) {
        return resolve(legacyOrderId).map(LegacyOrderIdMapping::internalOrderItemId);
    }
}
