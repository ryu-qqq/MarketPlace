package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.factory.OrderQueryFactory;
import com.ryuqq.marketplace.application.order.internal.OrderReadFacade;
import com.ryuqq.marketplace.application.order.internal.ProductOrderListBundle;
import com.ryuqq.marketplace.application.order.port.in.query.GetProductOrderListUseCase;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import org.springframework.stereotype.Service;

/**
 * 상품주문(아이템 단위) 목록 조회 Service (V5).
 *
 * <p>ReadFacade에서 번들을 조회하고, Assembler에서 최종 결과를 조립합니다.
 */
@Service
public class GetProductOrderListService implements GetProductOrderListUseCase {

    private final OrderReadFacade readFacade;
    private final OrderAssembler assembler;
    private final OrderQueryFactory queryFactory;

    public GetProductOrderListService(
            OrderReadFacade readFacade, OrderAssembler assembler, OrderQueryFactory queryFactory) {
        this.readFacade = readFacade;
        this.assembler = assembler;
        this.queryFactory = queryFactory;
    }

    @Override
    public ProductOrderPageResult execute(OrderSearchParams params) {
        OrderSearchCriteria criteria = queryFactory.createCriteria(params);
        ProductOrderListBundle bundle = readFacade.getProductOrderListBundle(criteria);
        return assembler.toProductOrderPageResult(bundle, params.page(), params.size());
    }
}
