package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDERS;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper.LegacyOrderCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;
import com.ryuqq.marketplace.application.legacy.order.port.in.command.LegacyOrderUpdateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 주문 수정 API 컨트롤러.
 *
 * <p>인증된 사용자만 접근 가능. 주문 소유자 검증은 서비스 레이어에서 처리.
 */
@Tag(name = "세토프 어드민용 레거시 - 주문", description = "세토프 어드민용 레거시 주문 엔드포인트.")
@RestController
public class LegacyOrderCommandController {

    private final LegacyOrderUpdateUseCase orderUpdateUseCase;
    private final LegacyOrderCommandApiMapper commandApiMapper;

    public LegacyOrderCommandController(
            LegacyOrderUpdateUseCase orderUpdateUseCase,
            LegacyOrderCommandApiMapper commandApiMapper) {
        this.orderUpdateUseCase = orderUpdateUseCase;
        this.commandApiMapper = commandApiMapper;
    }

    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다. (배송, 취소승인/반려, 반품승인/반려 등)")
    @PreAuthorize("@legacyAccess.authenticated()")
    @PutMapping(ORDER)
    public ResponseEntity<LegacyApiResponse<LegacyUpdateOrderResponse>> modifyOrderStatus(
            @RequestBody LegacyUpdateOrderRequest request) {
        LegacyOrderUpdateCommand command = commandApiMapper.toCommand(request);
        LegacyOrderUpdateResult result = orderUpdateUseCase.execute(command);
        LegacyUpdateOrderResponse response = commandApiMapper.toResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }

    @Operation(summary = "주문 일괄 상태 변경", description = "여러 주문의 상태를 일괄 변경합니다.")
    @PreAuthorize("@legacyAccess.authenticated()")
    @PutMapping(ORDERS)
    public ResponseEntity<LegacyApiResponse<List<LegacyUpdateOrderResponse>>> modifyOrderStatusList(
            @RequestBody List<LegacyUpdateOrderRequest> requests) {
        List<LegacyUpdateOrderResponse> responses = requests.stream()
                .map(request -> {
                    LegacyOrderUpdateCommand command = commandApiMapper.toCommand(request);
                    LegacyOrderUpdateResult result = orderUpdateUseCase.execute(command);
                    return commandApiMapper.toResponse(result);
                })
                .toList();
        return ResponseEntity.ok(LegacyApiResponse.success(responses));
    }
}
