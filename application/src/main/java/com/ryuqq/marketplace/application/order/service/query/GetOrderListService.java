package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderPageResult;
import com.ryuqq.marketplace.application.order.factory.OrderQueryFactory;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderListUseCase;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 주문 목록 조회 Service. */
@Service
public class GetOrderListService implements GetOrderListUseCase {

    private final OrderCompositionReadManager compositionReadManager;
    private final OrderQueryFactory queryFactory;

    public GetOrderListService(
            OrderCompositionReadManager compositionReadManager, OrderQueryFactory queryFactory) {
        this.compositionReadManager = compositionReadManager;
        this.queryFactory = queryFactory;
    }

    @Override
    public OrderPageResult execute(OrderSearchParams params) {
        OrderSearchCriteria criteria = queryFactory.createCriteria(params);

        List<OrderListResult> orders = compositionReadManager.searchOrders(criteria);
        long totalElements = compositionReadManager.countOrders(criteria);

        PageMeta pageMeta = PageMeta.of(params.searchParams().page(), params.searchParams().size(), totalElements);
        return new OrderPageResult(orders, pageMeta);
    }
}
