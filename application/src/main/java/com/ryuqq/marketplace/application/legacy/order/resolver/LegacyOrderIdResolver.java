package com.ryuqq.marketplace.application.legacy.order.resolver;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 ID ↔ Market 주문 ID 양방향 리졸버.
 *
 * <p>legacy_order_id_mappings 테이블에서 직접 조회합니다. legacyOrderId로 먼저 조회하고, 없으면
 * internalOrderItemId(market PK)로 재조회합니다.
 */
@Component
public class LegacyOrderIdResolver {

    private final LegacyOrderIdMappingReadManager mappingReadManager;

    public LegacyOrderIdResolver(LegacyOrderIdMappingReadManager mappingReadManager) {
        this.mappingReadManager = mappingReadManager;
    }

    /**
     * orderId → market 매핑 조회. legacyOrderId로 먼저 찾고, 없으면 market orderItemId로 재조회.
     *
     * @param orderId 레거시 주문 ID 또는 market orderItemId
     * @return 매핑 Optional
     */
    public Optional<LegacyOrderIdMapping> resolve(long orderId) {
        Optional<LegacyOrderIdMapping> byLegacy = mappingReadManager.findByLegacyOrderId(orderId);
        if (byLegacy.isPresent()) {
            return byLegacy;
        }
        List<LegacyOrderIdMapping> byInternal =
                mappingReadManager.findByInternalOrderItemIds(List.of(orderId));
        return byInternal.isEmpty() ? Optional.empty() : Optional.of(byInternal.getFirst());
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
