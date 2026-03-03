package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderPageResult;
import com.ryuqq.marketplace.application.order.factory.OrderQueryFactory;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderListUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 주문 목록 조회 Service. */
@Service
public class GetOrderListService implements GetOrderListUseCase {

    private final OrderReadManager readManager;
    private final OrderQueryFactory queryFactory;
    private final OrderAssembler assembler;

    public GetOrderListService(
            OrderReadManager readManager,
            OrderQueryFactory queryFactory,
            OrderAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public OrderPageResult execute(OrderSearchParams params) {
        OrderSearchCriteria criteria = queryFactory.createCriteria(params);

        List<Order> orders = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(orders, params.page(), params.size(), totalElements);
    }
}
