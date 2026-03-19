package com.ryuqq.marketplace.adapter.in.rest.exchange.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryMemoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ApproveExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CollectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CompleteExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ConvertToRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.HoldExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.PrepareExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RejectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RequestExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ShipExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.mapper.ExchangeApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.port.in.command.ApproveExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.CollectExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.CompleteExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.ConvertToRefundBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.HoldExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.PrepareExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.RejectExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.RequestExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.ShipExchangeBatchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 교환 커맨드 API 컨트롤러. */
@Tag(name = "교환 명령", description = "교환 명령 API")
@RestController
@RequestMapping(ExchangeAdminEndpoints.EXCHANGES)
@SuppressWarnings("PMD.TooManyMethods")
public class ExchangeCommandController {

    private final RequestExchangeBatchUseCase requestExchangeBatchUseCase;
    private final ApproveExchangeBatchUseCase approveExchangeBatchUseCase;
    private final CollectExchangeBatchUseCase collectExchangeBatchUseCase;
    private final PrepareExchangeBatchUseCase prepareExchangeBatchUseCase;
    private final RejectExchangeBatchUseCase rejectExchangeBatchUseCase;
    private final ShipExchangeBatchUseCase shipExchangeBatchUseCase;
    private final CompleteExchangeBatchUseCase completeExchangeBatchUseCase;
    private final ConvertToRefundBatchUseCase convertToRefundBatchUseCase;
    private final HoldExchangeBatchUseCase holdExchangeBatchUseCase;
    private final AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    private final ExchangeApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public ExchangeCommandController(
            RequestExchangeBatchUseCase requestExchangeBatchUseCase,
            ApproveExchangeBatchUseCase approveExchangeBatchUseCase,
            CollectExchangeBatchUseCase collectExchangeBatchUseCase,
            PrepareExchangeBatchUseCase prepareExchangeBatchUseCase,
            RejectExchangeBatchUseCase rejectExchangeBatchUseCase,
            ShipExchangeBatchUseCase shipExchangeBatchUseCase,
            CompleteExchangeBatchUseCase completeExchangeBatchUseCase,
            ConvertToRefundBatchUseCase convertToRefundBatchUseCase,
            HoldExchangeBatchUseCase holdExchangeBatchUseCase,
            AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase,
            ExchangeApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.requestExchangeBatchUseCase = requestExchangeBatchUseCase;
        this.approveExchangeBatchUseCase = approveExchangeBatchUseCase;
        this.collectExchangeBatchUseCase = collectExchangeBatchUseCase;
        this.prepareExchangeBatchUseCase = prepareExchangeBatchUseCase;
        this.rejectExchangeBatchUseCase = rejectExchangeBatchUseCase;
        this.shipExchangeBatchUseCase = shipExchangeBatchUseCase;
        this.completeExchangeBatchUseCase = completeExchangeBatchUseCase;
        this.convertToRefundBatchUseCase = convertToRefundBatchUseCase;
        this.holdExchangeBatchUseCase = holdExchangeBatchUseCase;
        this.addClaimHistoryMemoUseCase = addClaimHistoryMemoUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    @Operation(summary = "교환 요청 일괄 처리", description = "선택한 주문상품의 교환을 일괄 요청합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 요청 일괄")
    @PostMapping(ExchangeAdminEndpoints.REQUEST_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> requestBatch(
            @RequestBody @Valid RequestExchangeBatchApiRequest request) {
        Long sellerIdOrNull = accessChecker.resolveSellerIdOrNull();
        long sellerId = sellerIdOrNull != null ? sellerIdOrNull : 0L;
        String requestedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                requestExchangeBatchUseCase.execute(
                        mapper.toRequestExchangeBatchCommand(request, requestedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 승인 일괄 처리", description = "교환 요청을 일괄 승인합니다 (수거 시작).")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 승인 일괄")
    @PostMapping(ExchangeAdminEndpoints.APPROVE_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> approveBatch(
            @RequestBody @Valid ApproveExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                approveExchangeBatchUseCase.execute(
                        mapper.toApproveExchangeBatchCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 수거 완료 일괄 처리", description = "교환 상품 수거 완료를 일괄 처리합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 수거 완료 일괄")
    @PostMapping(ExchangeAdminEndpoints.COLLECT_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> collectBatch(
            @RequestBody @Valid CollectExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                collectExchangeBatchUseCase.execute(
                        mapper.toCollectExchangeBatchCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 준비 완료 일괄 처리", description = "교환 상품 준비 완료를 일괄 처리합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 준비 완료 일괄")
    @PostMapping(ExchangeAdminEndpoints.PREPARE_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> prepareBatch(
            @RequestBody @Valid PrepareExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                prepareExchangeBatchUseCase.execute(
                        mapper.toPrepareExchangeBatchCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 거절 일괄 처리", description = "교환 요청을 일괄 거절합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 거절 일괄")
    @PostMapping(ExchangeAdminEndpoints.REJECT_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> rejectBatch(
            @RequestBody @Valid RejectExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                rejectExchangeBatchUseCase.execute(
                        mapper.toRejectExchangeBatchCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 재배송 일괄 처리", description = "교환 상품 재배송을 일괄 처리합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 재배송 일괄")
    @PostMapping(ExchangeAdminEndpoints.SHIP_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> shipBatch(
            @RequestBody @Valid ShipExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                shipExchangeBatchUseCase.execute(
                        mapper.toShipCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 완료 일괄 처리", description = "교환을 일괄 완료 처리합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 완료 일괄")
    @PostMapping(ExchangeAdminEndpoints.COMPLETE_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> completeBatch(
            @RequestBody @Valid CompleteExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                completeExchangeBatchUseCase.execute(
                        mapper.toCompleteCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 건 환불 전환 일괄 처리", description = "교환 건을 취소하고 환불로 전환합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환→환불 전환 일괄")
    @PostMapping(ExchangeAdminEndpoints.CONVERT_TO_REFUND_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> convertToRefundBatch(
            @RequestBody @Valid ConvertToRefundBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                convertToRefundBatchUseCase.execute(
                        mapper.toConvertToRefundCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 보류/보류 해제 일괄 처리", description = "교환 건을 일괄 보류하거나 보류 해제합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 보류/보류 해제 일괄")
    @PatchMapping(ExchangeAdminEndpoints.HOLD_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> holdBatch(
            @RequestBody @Valid HoldExchangeBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                holdExchangeBatchUseCase.execute(
                        mapper.toHoldCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "교환 수기 메모 등록", description = "교환 건에 수기 메모를 등록합니다.")
    @PreAuthorize("@access.hasPermission('exchange:write')")
    @RequirePermission(value = "exchange:write", description = "교환 수기 메모 등록")
    @PostMapping(ExchangeAdminEndpoints.HISTORIES)
    public ResponseEntity<ApiResponse<ClaimHistoryMemoApiResponse>> addMemo(
            @PathVariable String exchangeClaimId,
            @RequestBody @Valid AddClaimHistoryMemoApiRequest request) {
        Long sellerIdOrNull = accessChecker.resolveSellerIdOrNull();
        long sellerId = sellerIdOrNull != null ? sellerIdOrNull : 0L;
        String actorName = resolveCurrentUsername();
        AddClaimHistoryMemoCommand command =
                mapper.toAddMemoCommand(exchangeClaimId, request, sellerId, actorName);
        String historyId = addClaimHistoryMemoUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new ClaimHistoryMemoApiResponse(historyId)));
    }

    private String resolveCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "SYSTEM";
    }
}
