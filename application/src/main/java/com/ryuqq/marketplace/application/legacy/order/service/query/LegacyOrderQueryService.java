package com.ryuqq.marketplace.application.legacy.order.service.query;

import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.manager.LegacyOrderReadManager;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 주문 단건 조회 서비스. */
@Service
public class LegacyOrderQueryService implements LegacyOrderQueryUseCase {

    private final LegacyOrderReadManager readManager;

    public LegacyOrderQueryService(LegacyOrderReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyOrderDetailResult execute(long orderId) {
        return readManager.fetchOrderDetail(orderId);
    }
}
