package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDERS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_HISTORY;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyOrderSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.OrderHistoryInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper.LegacyOrderQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.order.assembler.LegacyOrderFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderPageResult;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderListQueryUseCase;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase;
import com.ryuqq.marketplace.application.legacy.order.resolver.LegacyOrderIdResolver;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 주문 조회 API 컨트롤러.
 *
 * <p>MASTER는 전체 주문 조회, SELLER는 본인 주문만 조회 가능.
 */
@Tag(name = "세토프 어드민용 레거시 - 주문", description = "세토프 어드민용 레거시 주문 엔드포인트.")
@RestController
public class LegacyOrderQueryController {

    private final LegacyOrderQueryUseCase orderQueryUseCase;
    private final LegacyOrderListQueryUseCase orderListQueryUseCase;
    private final LegacyOrderQueryApiMapper queryApiMapper;
    private final LegacyAccessChecker legacyAccessChecker;
    private final LegacyOrderIdResolver idResolver;
    private final GetOrderDetailUseCase getOrderDetailUseCase;
    private final ShipmentReadManager shipmentReadManager;
    private final LegacyOrderFromMarketAssembler assembler;

    public LegacyOrderQueryController(
            LegacyOrderQueryUseCase orderQueryUseCase,
            LegacyOrderListQueryUseCase orderListQueryUseCase,
            LegacyOrderQueryApiMapper queryApiMapper,
            LegacyAccessChecker legacyAccessChecker,
            LegacyOrderIdResolver idResolver,
            GetOrderDetailUseCase getOrderDetailUseCase,
            ShipmentReadManager shipmentReadManager,
            LegacyOrderFromMarketAssembler assembler) {
        this.orderQueryUseCase = orderQueryUseCase;
        this.orderListQueryUseCase = orderListQueryUseCase;
        this.queryApiMapper = queryApiMapper;
        this.legacyAccessChecker = legacyAccessChecker;
        this.idResolver = idResolver;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
        this.shipmentReadManager = shipmentReadManager;
        this.assembler = assembler;
    }

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 주문 상세 정보를 조회합니다.")
    @PreAuthorize("@legacyAccess.authenticated()")
    @GetMapping(ORDER_ID)
    public ResponseEntity<LegacyApiResponse<LegacyOrderResponse>> fetchOrder(
            @PathVariable long orderId) {
        LegacyOrderDetailResult result = orderQueryUseCase.execute(orderId);

        if (!legacyAccessChecker.isMaster()
                && result.sellerId() != legacyAccessChecker.getCurrentSellerId()) {
            throw new AccessDeniedException("해당 주문에 접근 권한이 없습니다");
        }

        LegacyOrderResponse response = queryApiMapper.toOrderResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }

    @Operation(summary = "주문 이력 조회", description = "주문의 전체 타임라인(상태변경+취소+반품+배송)을 조회합니다.")
    @PreAuthorize("@legacyAccess.authenticated()")
    @GetMapping(ORDER_HISTORY)
    public ResponseEntity<LegacyApiResponse<List<OrderHistoryInfo>>> getOrderHistory(
            @PathVariable long orderId) {

        LegacyOrderIdMapping mapping = idResolver
                .resolve(orderId)
                .orElseThrow(() -> new com.ryuqq.marketplace.domain.order.exception
                        .OrderNotFoundException(String.valueOf(orderId)));

        Long orderItemId = mapping.internalOrderItemId();
        var detail = getOrderDetailUseCase.execute(orderItemId);

        Shipment shipment = shipmentReadManager
                .findByOrderItemId(OrderItemId.of(orderItemId))
                .orElse(null);

        List<LegacyOrderHistoryResult> timeline =
                assembler.toUnifiedTimeline(detail, mapping.legacyOrderId(), shipment);

        List<OrderHistoryInfo> response = timeline.stream()
                .map(h -> queryApiMapper.toOrderHistoryInfo(h))
                .toList();

        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }

    @Operation(summary = "주문 목록 조회", description = "커서 기반 페이징으로 주문 목록을 조회합니다.")
    @PreAuthorize("@legacyAccess.authenticated()")
    @GetMapping(ORDERS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyOrderResponse>>> getOrders(
            @ModelAttribute LegacyOrderSearchRequest request) {

        Long effectiveSellerId = legacyAccessChecker.resolveSellerIdOrNull();
        LegacyOrderSearchParams params = queryApiMapper.toSearchParams(request, effectiveSellerId);

        LegacyOrderPageResult pageResult = orderListQueryUseCase.execute(params);

        List<LegacyOrderResponse> responses =
                queryApiMapper.toOrderListResponses(pageResult.items());

        Pageable pageable = PageRequest.of(0, params.size());
        LegacyCustomPageable<LegacyOrderResponse> page =
                new LegacyCustomPageable<>(
                        responses, pageable, pageResult.totalElements(), pageResult.lastDomainId());

        return ResponseEntity.ok(LegacyApiResponse.success(page));
    }
}
