package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderIdScanQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderIdScanPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderScanEntry;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 ID 커서 기반 스캔 Adapter.
 *
 * <p>{@link LegacyOrderIdScanPort} 구현체.
 * orders 테이블에서 활성 주문 엔트리(orderId + paymentId)를 커서 기반으로 스캔합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyOrderIdScanAdapter implements LegacyOrderIdScanPort {

    private final LegacyOrderIdScanQueryDslRepository queryDslRepository;

    public LegacyOrderIdScanAdapter(LegacyOrderIdScanQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public List<LegacyOrderScanEntry> findActiveOrderEntries(long afterId, int limit) {
        return queryDslRepository.findActiveOrderEntries(afterId, limit);
    }
}
