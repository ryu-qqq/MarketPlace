package com.ryuqq.marketplace.adapter.in.rest.order.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.mapper.OrderQueryApiMapper;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderSummaryUseCase;
import com.ryuqq.marketplace.application.order.port.in.query.GetProductOrderListUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 주문 조회 Controller (V5). */
@Tag(name = "주문 조회", description = "주문 조회 API")
@RestController
@RequestMapping(OrderAdminEndpoints.ORDERS)
public class OrderQueryController {

    private final GetProductOrderListUseCase getProductOrderListUseCase;
    private final GetOrderDetailUseCase getOrderDetailUseCase;
    private final GetOrderSummaryUseCase getOrderSummaryUseCase;
    private final OrderQueryApiMapper mapper;

    public OrderQueryController(
            GetProductOrderListUseCase getProductOrderListUseCase,
            GetOrderDetailUseCase getOrderDetailUseCase,
            GetOrderSummaryUseCase getOrderSummaryUseCase,
            OrderQueryApiMapper mapper) {
        this.getProductOrderListUseCase = getProductOrderListUseCase;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
        this.getOrderSummaryUseCase = getOrderSummaryUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "상품주문 목록 조회", description = "상품주문(아이템) 단위로 목록을 페이지 조회합니다.")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<OrderListApiResponse>>> searchOrders(
            @Valid @ParameterObject SearchOrdersApiRequest request) {

        OrderSearchParams params = mapper.toSearchParams(request);
        ProductOrderPageResult pageResult = getProductOrderListUseCase.execute(params);
        PageApiResponse<OrderListApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "상품주문 상세 조회", description = "상품주문(아이템) 단위 상세 정보를 조회합니다.")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 상세 조회")
    @GetMapping(OrderAdminEndpoints.ORDER_ITEM_ID)
    public ResponseEntity<ApiResponse<OrderDetailApiResponse>> getOrderDetail(
            @Positive @PathVariable(OrderAdminEndpoints.PATH_ORDER_ITEM_ID) long orderItemId) {

        ProductOrderDetailResult result = getOrderDetailUseCase.execute(orderItemId);
        OrderDetailApiResponse response = mapper.toDetailResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "주문 상태별 요약 조회", description = "주문 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 요약 조회")
    @GetMapping(OrderAdminEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<OrderSummaryApiResponse>> getSummary() {

        OrderSummaryResult result = getOrderSummaryUseCase.execute();
        OrderSummaryApiResponse response = mapper.toSummaryResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
