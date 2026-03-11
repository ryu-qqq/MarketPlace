package com.ryuqq.marketplace.adapter.in.rest.shipment.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ConfirmShipmentBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipSingleApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.mapper.ShipmentCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.port.in.command.ConfirmShipmentBatchUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipBatchUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipSingleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 배송 커맨드 API 컨트롤러. */
@Tag(name = "배송 명령", description = "배송 명령 API")
@RestController
@RequestMapping(ShipmentEndpoints.SHIPMENTS)
public class ShipmentCommandController {

    private final ConfirmShipmentBatchUseCase confirmShipmentBatchUseCase;
    private final ShipBatchUseCase shipBatchUseCase;
    private final ShipSingleUseCase shipSingleUseCase;
    private final ShipmentCommandApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public ShipmentCommandController(
            ConfirmShipmentBatchUseCase confirmShipmentBatchUseCase,
            ShipBatchUseCase shipBatchUseCase,
            ShipSingleUseCase shipSingleUseCase,
            ShipmentCommandApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.confirmShipmentBatchUseCase = confirmShipmentBatchUseCase;
        this.shipBatchUseCase = shipBatchUseCase;
        this.shipSingleUseCase = shipSingleUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    @Operation(summary = "발주확인 일괄 처리", description = "선택한 상품주문의 발주를 일괄 확인합니다.")
    @PreAuthorize("@access.hasPermission('shipment:write')")
    @RequirePermission(value = "shipment:write", description = "발주확인 일괄 처리")
    @PostMapping(ShipmentEndpoints.CONFIRM_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> confirmBatch(
            @RequestBody @Valid ConfirmShipmentBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        BatchProcessingResult<String> result =
                confirmShipmentBatchUseCase.execute(
                        mapper.toConfirmBatchCommand(request, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "송장등록 일괄 처리", description = "선택한 상품주문에 송장을 일괄 등록합니다.")
    @PreAuthorize("@access.hasPermission('shipment:write')")
    @RequirePermission(value = "shipment:write", description = "송장등록 일괄 처리")
    @PostMapping(ShipmentEndpoints.SHIP_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> shipBatch(
            @RequestBody @Valid ShipBatchApiRequest request) {
        BatchProcessingResult<String> result =
                shipBatchUseCase.execute(mapper.toShipBatchCommand(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "단건 송장등록", description = "상품주문에 대해 송장을 등록합니다.")
    @PreAuthorize("@access.hasPermission('shipment:write')")
    @RequirePermission(value = "shipment:write", description = "단건 송장등록")
    @PostMapping(ShipmentEndpoints.SHIP_SINGLE)
    public ResponseEntity<ApiResponse<Void>> shipSingle(
            @PathVariable Long orderItemId, @RequestBody @Valid ShipSingleApiRequest request) {
        shipSingleUseCase.execute(mapper.toShipSingleCommand(orderItemId, request));
        return ResponseEntity.ok(ApiResponse.of());
    }
}
