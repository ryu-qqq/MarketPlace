package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.internal.OrderReadFacade;
import com.ryuqq.marketplace.application.order.internal.ProductOrderDetailBundle;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import org.springframework.stereotype.Service;

/** 상품주문 상세 조회 Service (V5). */
@Service
public class GetOrderDetailService implements GetOrderDetailUseCase {

    private final OrderReadFacade readFacade;
    private final OrderAssembler assembler;

    public GetOrderDetailService(OrderReadFacade readFacade, OrderAssembler assembler) {
        this.readFacade = readFacade;
        this.assembler = assembler;
    }

    @Override
    public ProductOrderDetailResult execute(String orderItemId) {
        ProductOrderDetailBundle bundle = readFacade.getProductOrderDetailBundle(orderItemId);
        return assembler.toProductOrderDetailResult(bundle);
    }
}
