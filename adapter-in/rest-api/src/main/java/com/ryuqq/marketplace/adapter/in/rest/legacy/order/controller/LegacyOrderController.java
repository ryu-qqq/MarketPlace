package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDERS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_DATE_DASHBOARD;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_HISTORY;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_TODAY_DASHBOARD;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.SETTLEMENTS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.SHIPMENT_ORDER;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyShipmentInfoRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 레거시 주문 API 호환 컨트롤러. */
@RestController
public class LegacyOrderController {

    // ===== 조회 =====

    @GetMapping(ORDER_ID)
    public ResponseEntity<ApiResponse<Object>> fetchOrder(@PathVariable long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDERS)
    public ResponseEntity<ApiResponse<Object>> getOrders(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(SETTLEMENTS)
    public ResponseEntity<ApiResponse<Object>> getSettlements(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDER_HISTORY)
    public ResponseEntity<ApiResponse<Object>> getOrderHistory(@PathVariable long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDER_TODAY_DASHBOARD)
    public ResponseEntity<ApiResponse<Object>> getOrderTodayDashboard() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(ORDER_DATE_DASHBOARD)
    public ResponseEntity<ApiResponse<Object>> getOrderDateDashboard() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ===== 수정 =====

    @PutMapping(ORDER)
    public ResponseEntity<ApiResponse<LegacyUpdateOrderResponse>> modifyOrderStatus(
            @RequestBody LegacyUpdateOrderRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(ORDERS)
    public ResponseEntity<ApiResponse<List<LegacyUpdateOrderResponse>>> modifyOrderStatusList(
            @RequestBody List<LegacyUpdateOrderRequest> requests) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(SHIPMENT_ORDER)
    public ResponseEntity<ApiResponse<Object>> updateShipment(
            @PathVariable long orderId, @RequestBody LegacyShipmentInfoRequest shipmentInfo) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
