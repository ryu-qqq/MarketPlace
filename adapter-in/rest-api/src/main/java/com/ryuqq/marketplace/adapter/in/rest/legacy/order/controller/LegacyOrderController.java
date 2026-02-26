package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDERS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderListResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 주문 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다. OMS(사방넷, 셀릭)가 호출하는 GET /orders, GET /order/{id}, PUT
 * /order만 제공합니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyOrderController {

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 주문 상세 정보를 조회합니다.")
    @GetMapping(ORDER_ID)
    public ResponseEntity<LegacyApiResponse<LegacyOrderResponse>> fetchOrder(
            @PathVariable long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(summary = "주문 목록 조회", description = "페이징 기반으로 주문 목록을 조회합니다.")
    @GetMapping(ORDERS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyOrderListResponse>>>
            getOrders(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다. (정상, 배송, 클레임 등)")
    @PutMapping(ORDER)
    public ResponseEntity<LegacyApiResponse<LegacyUpdateOrderResponse>> modifyOrderStatus(
            @RequestBody LegacyUpdateOrderRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
