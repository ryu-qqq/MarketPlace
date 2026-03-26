package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyExternalOrderJpaRepository;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderQueryPort;
import org.springframework.stereotype.Component;

/**
 * 셀릭 주문 중복 확인 Adapter.
 *
 * <p>{@link SellicLegacyOrderQueryPort} 구현체. external_order 테이블에서 셀릭 IDX 중복을 확인합니다.
 */
@Component
public class SellicLegacyOrderQueryAdapter implements SellicLegacyOrderQueryPort {

    private final LegacyExternalOrderJpaRepository externalOrderRepository;

    public SellicLegacyOrderQueryAdapter(LegacyExternalOrderJpaRepository externalOrderRepository) {
        this.externalOrderRepository = externalOrderRepository;
    }

    @Override
    public boolean existsByExternalIdx(long siteId, long externalIdx) {
        return externalOrderRepository.existsBySiteIdAndExternalIdx(siteId, externalIdx);
    }
}
