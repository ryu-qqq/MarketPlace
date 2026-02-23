package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDERS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_DATE_DASHBOARD;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_HISTORY;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_TODAY_DASHBOARD;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.SETTLEMENTS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.SHIPMENT_ORDER;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyShipmentInfoRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderDashboardResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderDateDashboardResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderHistoryResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderListResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacySettlementResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyShipmentInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 주문 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyOrderController {

    // ===== 조회 =====

    @GetMapping(ORDER_ID)
    public ResponseEntity<LegacyApiResponse<LegacyOrderResponse>> fetchOrder(
            @PathVariable long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDERS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyOrderListResponse>>>
            getOrders(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(SETTLEMENTS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacySettlementResponse>>>
            getSettlements(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDER_HISTORY)
    public ResponseEntity<LegacyApiResponse<List<LegacyOrderHistoryResponse>>> getOrderHistory(
            @PathVariable long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDER_TODAY_DASHBOARD)
    public ResponseEntity<LegacyApiResponse<LegacyOrderDashboardResponse>>
            getOrderTodayDashboard() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDER_DATE_DASHBOARD)
    public ResponseEntity<LegacyApiResponse<LegacyOrderDateDashboardResponse>>
            getOrderDateDashboard() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ===== 수정 =====

    @PutMapping(ORDER)
    public ResponseEntity<LegacyApiResponse<LegacyUpdateOrderResponse>> modifyOrderStatus(
            @RequestBody LegacyUpdateOrderRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(ORDERS)
    public ResponseEntity<LegacyApiResponse<List<LegacyUpdateOrderResponse>>> modifyOrderStatusList(
            @RequestBody List<LegacyUpdateOrderRequest> requests) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(SHIPMENT_ORDER)
    public ResponseEntity<LegacyApiResponse<LegacyShipmentInfoResponse>> updateShipment(
            @PathVariable long orderId, @RequestBody LegacyShipmentInfoRequest shipmentInfo) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
