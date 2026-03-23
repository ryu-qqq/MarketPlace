package com.ryuqq.marketplace.adapter.in.rest.shipment.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.mapper.ShipmentQueryApiMapper;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentDetailUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentListUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentSummaryUseCase;
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

/** 배송 조회 API 컨트롤러. */
@Tag(name = "배송 조회", description = "배송 조회 API")
@RestController
@RequestMapping(ShipmentEndpoints.SHIPMENTS)
public class ShipmentQueryController {

    private final GetShipmentSummaryUseCase getShipmentSummaryUseCase;
    private final GetShipmentListUseCase getShipmentListUseCase;
    private final GetShipmentDetailUseCase getShipmentDetailUseCase;
    private final ShipmentQueryApiMapper mapper;

    public ShipmentQueryController(
            GetShipmentSummaryUseCase getShipmentSummaryUseCase,
            GetShipmentListUseCase getShipmentListUseCase,
            GetShipmentDetailUseCase getShipmentDetailUseCase,
            ShipmentQueryApiMapper mapper) {
        this.getShipmentSummaryUseCase = getShipmentSummaryUseCase;
        this.getShipmentListUseCase = getShipmentListUseCase;
        this.getShipmentDetailUseCase = getShipmentDetailUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "배송 상태별 요약 조회", description = "배송 상태별 건수를 요약 조회합니다.")
    @PreAuthorize("@access.hasPermission('shipment:read')")
    @RequirePermission(value = "shipment:read", description = "배송 요약 조회")
    @GetMapping(ShipmentEndpoints.SUMMARY)
    public ResponseEntity<ApiResponse<ShipmentSummaryApiResponse>> getSummary() {
        ShipmentSummaryResult result = getShipmentSummaryUseCase.execute();
        return ResponseEntity.ok(ApiResponse.of(mapper.toSummaryResponse(result)));
    }

    @Operation(summary = "배송 목록 조회", description = "배송 목록을 검색 조건에 따라 조회합니다.")
    @PreAuthorize("@access.hasPermission('shipment:read')")
    @RequirePermission(value = "shipment:read", description = "배송 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ShipmentListApiResponseV4>>> searchShipments(
            @ParameterObject @Valid ShipmentSearchApiRequest request) {
        ShipmentPageResult pageResult =
                getShipmentListUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponseV4(pageResult)));
    }

    @Operation(summary = "배송 상세 조회", description = "배송 상세 정보를 조회합니다.")
    @PreAuthorize("@access.hasPermission('shipment:read')")
    @RequirePermission(value = "shipment:read", description = "배송 상세 조회")
    @GetMapping(ShipmentEndpoints.SHIPMENT_ID)
    public ResponseEntity<ApiResponse<ShipmentDetailApiResponse>> getShipment(
            @PathVariable String shipmentId) {
        ShipmentDetailResult result = getShipmentDetailUseCase.execute(shipmentId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toDetailResponse(result)));
    }
}
