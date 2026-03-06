package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.PATH_OUTBOX_ID;
import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.SYNC;
import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.SYNC_HISTORY_RETRY;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.command.SyncProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.RetrySyncApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncProductsApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsProductCommandApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import com.ryuqq.marketplace.application.outboundproduct.port.in.command.ManualSyncProductsUseCase;
import com.ryuqq.marketplace.application.outboundproduct.port.in.command.RetryOutboundSyncUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** OMS 상품 커맨드 컨트롤러 (API 4, 5). */
@RestController
@Tag(name = "OMS Product Command", description = "OMS 상품 커맨드 API")
public class OmsProductCommandController {

    private final RetryOutboundSyncUseCase retryOutboundSyncUseCase;
    private final ManualSyncProductsUseCase manualSyncProductsUseCase;
    private final OmsProductCommandApiMapper mapper;

    public OmsProductCommandController(
            RetryOutboundSyncUseCase retryOutboundSyncUseCase,
            ManualSyncProductsUseCase manualSyncProductsUseCase,
            OmsProductCommandApiMapper mapper) {
        this.retryOutboundSyncUseCase = retryOutboundSyncUseCase;
        this.manualSyncProductsUseCase = manualSyncProductsUseCase;
        this.mapper = mapper;
    }

    @PostMapping(SYNC_HISTORY_RETRY)
    @Operation(summary = "연동 재처리")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<RetrySyncApiResponse>> retrySyncHistory(
            @Parameter(description = "Outbox ID") @PathVariable(PATH_OUTBOX_ID) long outboxId) {
        retryOutboundSyncUseCase.execute(outboxId);
        RetrySyncApiResponse response = mapper.toRetryResponse(outboxId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping(SYNC)
    @Operation(summary = "상품 외부몰 전송")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<SyncProductsApiResponse>> syncProducts(
            @Valid @RequestBody SyncProductsApiRequest request) {
        ManualSyncProductsCommand command = mapper.toCommand(request);
        ManualSyncResult result = manualSyncProductsUseCase.execute(command);
        SyncProductsApiResponse response = mapper.toSyncResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
