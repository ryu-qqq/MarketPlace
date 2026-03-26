package com.ryuqq.marketplace.adapter.in.rest.cancel.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.ApproveCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.RejectCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.mapper.CancelApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryMemoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker.ActorInfo;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.port.in.command.ApproveCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.RejectCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.SellerCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelDetailUseCase;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 취소 커맨드 API 컨트롤러. */
@Tag(name = "취소 명령", description = "취소 명령 API")
@RestController
@RequestMapping(CancelAdminEndpoints.CANCELS)
public class CancelCommandController {

    private final SellerCancelBatchUseCase sellerCancelBatchUseCase;
    private final ApproveCancelBatchUseCase approveCancelBatchUseCase;
    private final RejectCancelBatchUseCase rejectCancelBatchUseCase;
    private final AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    private final GetCancelDetailUseCase getCancelDetailUseCase;
    private final CancelApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public CancelCommandController(
            SellerCancelBatchUseCase sellerCancelBatchUseCase,
            ApproveCancelBatchUseCase approveCancelBatchUseCase,
            RejectCancelBatchUseCase rejectCancelBatchUseCase,
            AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase,
            GetCancelDetailUseCase getCancelDetailUseCase,
            CancelApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.sellerCancelBatchUseCase = sellerCancelBatchUseCase;
        this.approveCancelBatchUseCase = approveCancelBatchUseCase;
        this.rejectCancelBatchUseCase = rejectCancelBatchUseCase;
        this.addClaimHistoryMemoUseCase = addClaimHistoryMemoUseCase;
        this.getCancelDetailUseCase = getCancelDetailUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    @Operation(summary = "판매자 취소 일괄 처리", description = "선택한 주문상품을 판매자가 일괄 취소합니다.")
    @PreAuthorize("@access.hasPermission('cancel:write')")
    @RequirePermission(value = "cancel:write", description = "판매자 취소 일괄")
    @PostMapping(CancelAdminEndpoints.SELLER_CANCEL_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> sellerCancelBatch(
            @RequestBody @Valid SellerCancelBatchApiRequest request) {
        ActorInfo actor = accessChecker.resolveActorInfo();
        BatchProcessingResult<String> result =
                sellerCancelBatchUseCase.execute(
                        mapper.toSellerCancelBatchCommand(request, actor.username(), actor.actorId()));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "취소 승인 일괄 처리", description = "구매자 취소 요청을 일괄 승인합니다.")
    @PreAuthorize("@access.hasPermission('cancel:write')")
    @RequirePermission(value = "cancel:write", description = "취소 승인 일괄")
    @PostMapping(CancelAdminEndpoints.APPROVE_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> approveBatch(
            @RequestBody @Valid ApproveCancelBatchApiRequest request) {
        ActorInfo actor = accessChecker.resolveActorInfo();
        BatchProcessingResult<String> result =
                approveCancelBatchUseCase.execute(
                        mapper.toApproveCancelBatchCommand(request, actor.username(), actor.sellerIdOrNull()));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "취소 거절 일괄 처리", description = "구매자 취소 요청을 일괄 거절합니다.")
    @PreAuthorize("@access.hasPermission('cancel:write')")
    @RequirePermission(value = "cancel:write", description = "취소 거절 일괄")
    @PostMapping(CancelAdminEndpoints.REJECT_BATCH)
    public ResponseEntity<ApiResponse<BatchResultApiResponse>> rejectBatch(
            @RequestBody @Valid RejectCancelBatchApiRequest request) {
        ActorInfo actor = accessChecker.resolveActorInfo();
        BatchProcessingResult<String> result =
                rejectCancelBatchUseCase.execute(
                        mapper.toRejectCancelBatchCommand(request, actor.username(), actor.sellerIdOrNull()));
        return ResponseEntity.ok(ApiResponse.of(mapper.toBatchResultResponse(result)));
    }

    @Operation(summary = "취소 수기 메모 등록", description = "취소 건에 수기 메모를 등록합니다.")
    @PreAuthorize("@access.hasPermission('cancel:write')")
    @RequirePermission(value = "cancel:write", description = "취소 수기 메모 등록")
    @PostMapping(CancelAdminEndpoints.HISTORIES)
    public ResponseEntity<ApiResponse<ClaimHistoryMemoApiResponse>> addMemo(
            @PathVariable String cancelId,
            @RequestBody @Valid AddClaimHistoryMemoApiRequest request) {
        CancelDetailResult detail = getCancelDetailUseCase.execute(cancelId);
        ActorInfo actor = accessChecker.resolveActorInfo();
        String historyId =
                addClaimHistoryMemoUseCase.execute(
                        mapper.toAddMemoCommand(cancelId, detail.orderItemId(), request, actor));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new ClaimHistoryMemoApiResponse(historyId)));
    }
}
