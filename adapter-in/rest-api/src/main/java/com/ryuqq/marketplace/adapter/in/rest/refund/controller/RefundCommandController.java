package com.ryuqq.marketplace.adapter.in.rest.refund.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryMemoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.ApproveRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RejectRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.mapper.RefundApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.port.in.command.ApproveRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.RejectRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.RequestRefundBatchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 환불 커맨드 API 컨트롤러. */
@Tag(name = "환불 명령", description = "환불 명령 API")
@RestController
@RequestMapping(RefundAdminEndpoints.REFUNDS)
public class RefundCommandController {

    private final RequestRefundBatchUseCase requestRefundBatchUseCase;
    private final ApproveRefundBatchUseCase approveRefundBatchUseCase;
    private final RejectRefundBatchUseCase rejectRefundBatchUseCase;
    private final AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    private final RefundApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public RefundCommandController(
            RequestRefundBatchUseCase requestRefundBatchUseCase,
            ApproveRefundBatchUseCase approveRefundBatchUseCase,
            RejectRefundBatchUseCase rejectRefundBatchUseCase,
            AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase,
            RefundApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.requestRefundBatchUseCase = requestRefundBatchUseCase;
        this.approveRefundBatchUseCase = approveRefundBatchUseCase;
        this.rejectRefundBatchUseCase = rejectRefundBatchUseCase;
        this.addClaimHistoryMemoUseCase = addClaimHistoryMemoUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    @Operation(summary = "환불 요청 일괄 처리", description = "선택한 주문상품의 환불을 일괄 요청합니다.")
    @PreAuthorize("@access.hasPermission('refund:write')")
    @RequirePermission(value = "refund:write", description = "환불 요청 일괄")
    @PostMapping(RefundAdminEndpoints.REQUEST_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> requestBatch(
            @RequestBody @Valid RequestRefundBatchApiRequest request) {
        long sellerId = accessChecker.resolveCurrentSellerId();
        String requestedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                requestRefundBatchUseCase.execute(
                        mapper.toRequestRefundBatchCommand(request, requestedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "환불 승인 일괄 처리", description = "환불 요청을 일괄 승인합니다 (수거 시작).")
    @PreAuthorize("@access.hasPermission('refund:write')")
    @RequirePermission(value = "refund:write", description = "환불 승인 일괄")
    @PostMapping(RefundAdminEndpoints.APPROVE_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> approveBatch(
            @RequestBody @Valid ApproveRefundBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                approveRefundBatchUseCase.execute(
                        mapper.toApproveRefundBatchCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "환불 거절 일괄 처리", description = "환불 요청을 일괄 거절합니다.")
    @PreAuthorize("@access.hasPermission('refund:write')")
    @RequirePermission(value = "refund:write", description = "환불 거절 일괄")
    @PostMapping(RefundAdminEndpoints.REJECT_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> rejectBatch(
            @RequestBody @Valid RejectRefundBatchApiRequest request) {
        Long sellerId = accessChecker.resolveSellerIdOrNull();
        String processedBy = resolveCurrentUsername();
        BatchProcessingResult<String> result =
                rejectRefundBatchUseCase.execute(
                        mapper.toRejectRefundBatchCommand(request, processedBy, sellerId));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "환불 수기 메모 등록", description = "환불 건에 수기 메모를 등록합니다.")
    @PreAuthorize("@access.hasPermission('refund:write')")
    @RequirePermission(value = "refund:write", description = "환불 수기 메모 등록")
    @PostMapping(RefundAdminEndpoints.HISTORIES)
    public ResponseEntity<ApiResponse<ClaimHistoryMemoApiResponse>> addMemo(
            @PathVariable String refundClaimId,
            @RequestBody @Valid AddClaimHistoryMemoApiRequest request) {
        long sellerId = accessChecker.resolveCurrentSellerId();
        String actorName = resolveCurrentUsername();
        AddClaimHistoryMemoCommand command =
                mapper.toAddMemoCommand(refundClaimId, request, sellerId, actorName);
        String historyId = addClaimHistoryMemoUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new ClaimHistoryMemoApiResponse(historyId)));
    }

    private String resolveCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "SYSTEM";
    }
}
