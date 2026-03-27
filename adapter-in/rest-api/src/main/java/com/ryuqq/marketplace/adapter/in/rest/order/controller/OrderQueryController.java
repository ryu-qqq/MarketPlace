package com.ryuqq.marketplace.adapter.in.rest.order.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrderClaimHistoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.order.mapper.OrderQueryApiMapper;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryPageResult;
import com.ryuqq.marketplace.application.claimhistory.port.in.query.GetOrderClaimHistoriesUseCase;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.order.port.in.query.GetProductOrderListUseCase;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final GetOrderClaimHistoriesUseCase getOrderClaimHistoriesUseCase;
    private final OrderQueryApiMapper mapper;

    public OrderQueryController(
            GetProductOrderListUseCase getProductOrderListUseCase,
            GetOrderDetailUseCase getOrderDetailUseCase,
            GetOrderClaimHistoriesUseCase getOrderClaimHistoriesUseCase,
            OrderQueryApiMapper mapper) {
        this.getProductOrderListUseCase = getProductOrderListUseCase;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
        this.getOrderClaimHistoriesUseCase = getOrderClaimHistoriesUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "상품주문 목록 조회", description = "상품주문(아이템) 단위로 목록을 페이지 조회합니다.")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<OrderListApiResponseV4>>> searchOrders(
            @Valid @ParameterObject SearchOrdersApiRequest request) {

        OrderSearchParams params = mapper.toSearchParams(request);
        ProductOrderPageResult pageResult = getProductOrderListUseCase.execute(params);
        PageApiResponse<OrderListApiResponseV4> response = mapper.toPageResponseV4(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(
            summary = "상품주문 상세 조회",
            description = "상품주문(아이템) 단위 상세. V4 스펙 형태로 반환 (경로 orderItemId, 실상 orderItem 데이터)")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 상세 조회")
    @GetMapping(OrderAdminEndpoints.ORDER_ITEM_ID)
    public ResponseEntity<ApiResponse<OrderDetailApiResponseV4>> getOrderDetail(
            @PathVariable(OrderAdminEndpoints.PATH_ORDER_ITEM_ID) String orderItemId) {

        ProductOrderDetailResult result = getOrderDetailUseCase.execute(orderItemId);
        OrderDetailApiResponseV4 response = mapper.toDetailResponseV4(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(
            summary = "주문 클레임 이력 조회",
            description = "주문 건에 대한 클레임 이력(메모 포함)을 페이지 조회합니다. claimType 필터 가능.")
    @PreAuthorize("@access.hasPermission('order:read')")
    @RequirePermission(value = "order:read", description = "주문 클레임 이력 조회")
    @GetMapping(OrderAdminEndpoints.HISTORIES)
    public ResponseEntity<ApiResponse<PageApiResponse<ClaimHistoryApiResponse>>> getClaimHistories(
            @PathVariable(OrderAdminEndpoints.PATH_ORDER_ITEM_ID) String orderItemId,
            @Valid @ParameterObject SearchOrderClaimHistoriesApiRequest request) {

        ClaimHistoryPageCriteria criteria = mapper.toClaimHistoryCriteria(orderItemId, request);
        ClaimHistoryPageResult result = getOrderClaimHistoriesUseCase.execute(criteria);
        PageApiResponse<ClaimHistoryApiResponse> response =
                mapper.toClaimHistoryPageResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
