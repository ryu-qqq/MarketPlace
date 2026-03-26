package com.ryuqq.marketplace.application.legacy.order.manager;

import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.port.out.query.LegacyOrderQueryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 조회 매니저.
 *
 * <p>LegacyOrderQueryPort 호출 래퍼.
 */
@Component
@Transactional(readOnly = true)
public class LegacyOrderReadManager {

    private final LegacyOrderQueryPort queryPort;

    public LegacyOrderReadManager(LegacyOrderQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    public LegacyOrderDetailResult fetchOrderDetail(long orderId) {
        return queryPort
                .fetchOrderDetail(orderId)
                .orElseThrow(
                        () -> new IllegalStateException("레거시 주문을 찾을 수 없습니다. orderId=" + orderId));
    }

    public Optional<LegacyOrderDetailResult> fetchOrderDetailOptional(long orderId) {
        return queryPort.fetchOrderDetail(orderId);
    }

    public List<LegacyOrderDetailResult> fetchOrderList(LegacyOrderSearchParams params) {
        return queryPort.fetchOrderList(params);
    }

    public long countOrders(LegacyOrderSearchParams params) {
        return queryPort.countOrders(params);
    }

    public List<LegacyOrderHistoryResult> fetchOrderHistories(List<Long> orderIds) {
        return queryPort.fetchOrderHistories(orderIds);
    }
}
