package com.ryuqq.marketplace.application.legacy.order.service.query;

import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailWithHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderPageResult;
import com.ryuqq.marketplace.application.legacy.order.manager.LegacyOrderReadManager;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderListQueryUseCase;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 목록 조회 서비스.
 *
 * <p>주문 목록 조회 + 카운트 + 히스토리 batch 조회 후 orderId별 그룹핑하여 PageResult 조립.
 */
@Service
public class LegacyOrderListQueryService implements LegacyOrderListQueryUseCase {

    private final LegacyOrderReadManager readManager;

    public LegacyOrderListQueryService(LegacyOrderReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyOrderPageResult execute(LegacyOrderSearchParams params) {
        List<LegacyOrderDetailResult> orders = readManager.fetchOrderList(params);
        long totalElements = readManager.countOrders(params);

        if (orders.isEmpty()) {
            return new LegacyOrderPageResult(List.of(), totalElements, null);
        }

        List<Long> orderIds = orders.stream().map(LegacyOrderDetailResult::orderId).toList();
        List<LegacyOrderHistoryResult> allHistories = readManager.fetchOrderHistories(orderIds);

        Map<Long, List<LegacyOrderHistoryResult>> historyMap =
                allHistories.stream()
                        .collect(Collectors.groupingBy(LegacyOrderHistoryResult::orderId));

        List<LegacyOrderDetailWithHistoryResult> items =
                orders.stream()
                        .map(
                                order ->
                                        new LegacyOrderDetailWithHistoryResult(
                                                order,
                                                historyMap.getOrDefault(
                                                        order.orderId(), List.of())))
                        .toList();

        Long lastDomainId = orders.getLast().orderId();

        return new LegacyOrderPageResult(items, totalElements, lastDomainId);
    }
}
