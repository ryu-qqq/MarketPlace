package com.ryuqq.marketplace.adapter.in.rest.settlement.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.SettlementAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.HoldSettlementApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementCompleteBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementHoldBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementReleaseBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.mapper.SettlementApiMapper;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.CompleteSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.HoldSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.ReleaseSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.application.settlement.port.in.command.HoldSettlementUseCase;
import com.ryuqq.marketplace.application.settlement.port.in.command.ReleaseSettlementUseCase;
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

/** 정산 Command API 컨트롤러. */
@Tag(name = "정산 관리", description = "정산 보류/해제/완료 처리 API")
@RestController
@RequestMapping(SettlementAdminEndpoints.SETTLEMENTS)
public class SettlementCommandController {

    private final HoldSettlementUseCase holdSettlementUseCase;
    private final ReleaseSettlementUseCase releaseSettlementUseCase;
    private final CompleteSettlementEntryBatchUseCase completeSettlementEntryBatchUseCase;
    private final HoldSettlementEntryBatchUseCase holdSettlementEntryBatchUseCase;
    private final ReleaseSettlementEntryBatchUseCase releaseSettlementEntryBatchUseCase;
    private final SettlementApiMapper mapper;

    public SettlementCommandController(
            HoldSettlementUseCase holdSettlementUseCase,
            ReleaseSettlementUseCase releaseSettlementUseCase,
            CompleteSettlementEntryBatchUseCase completeSettlementEntryBatchUseCase,
            HoldSettlementEntryBatchUseCase holdSettlementEntryBatchUseCase,
            ReleaseSettlementEntryBatchUseCase releaseSettlementEntryBatchUseCase,
            SettlementApiMapper mapper) {
        this.holdSettlementUseCase = holdSettlementUseCase;
        this.releaseSettlementUseCase = releaseSettlementUseCase;
        this.completeSettlementEntryBatchUseCase = completeSettlementEntryBatchUseCase;
        this.holdSettlementEntryBatchUseCase = holdSettlementEntryBatchUseCase;
        this.releaseSettlementEntryBatchUseCase = releaseSettlementEntryBatchUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "정산 보류", description = "개별 정산을 보류 처리합니다.")
    @PreAuthorize("@access.hasPermission('settlement:write')")
    @RequirePermission(value = "settlement:write", description = "정산 보류")
    @PostMapping(SettlementAdminEndpoints.HOLD)
    public ResponseEntity<ApiResponse<Void>> hold(
            @PathVariable String settlementId,
            @Valid @RequestBody HoldSettlementApiRequest request) {
        holdSettlementUseCase.execute(settlementId, request.reason());
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "정산 보류 해제", description = "보류된 정산을 해제합니다.")
    @PreAuthorize("@access.hasPermission('settlement:write')")
    @RequirePermission(value = "settlement:write", description = "정산 보류 해제")
    @PostMapping(SettlementAdminEndpoints.RELEASE)
    public ResponseEntity<ApiResponse<Void>> release(@PathVariable String settlementId) {
        releaseSettlementUseCase.execute(settlementId);
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "정산 원장 일괄 완료", description = "지정한 정산 원장 목록을 CONFIRMED 상태로 일괄 처리합니다.")
    @PreAuthorize("@access.hasPermission('settlement:write')")
    @RequirePermission(value = "settlement:write", description = "정산 일괄 완료")
    @PostMapping(SettlementAdminEndpoints.COMPLETE_BATCH)
    public ResponseEntity<ApiResponse<Void>> completeBatch(
            @Valid @RequestBody SettlementCompleteBatchApiRequest request) {
        completeSettlementEntryBatchUseCase.execute(mapper.toCompleteBatchCommand(request));
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "정산 원장 일괄 보류", description = "지정한 정산 원장 목록을 HOLD 상태로 일괄 처리합니다.")
    @PreAuthorize("@access.hasPermission('settlement:write')")
    @RequirePermission(value = "settlement:write", description = "정산 일괄 보류")
    @PostMapping(SettlementAdminEndpoints.HOLD_BATCH)
    public ResponseEntity<ApiResponse<Void>> holdBatch(
            @Valid @RequestBody SettlementHoldBatchApiRequest request) {
        holdSettlementEntryBatchUseCase.execute(mapper.toHoldBatchCommand(request));
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "정산 원장 일괄 보류 해제", description = "지정한 정산 원장 목록을 PENDING 상태로 일괄 복원합니다.")
    @PreAuthorize("@access.hasPermission('settlement:write')")
    @RequirePermission(value = "settlement:write", description = "정산 일괄 보류 해제")
    @PostMapping(SettlementAdminEndpoints.RELEASE_BATCH)
    public ResponseEntity<ApiResponse<Void>> releaseBatch(
            @Valid @RequestBody SettlementReleaseBatchApiRequest request) {
        releaseSettlementEntryBatchUseCase.execute(mapper.toReleaseBatchCommand(request));
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
