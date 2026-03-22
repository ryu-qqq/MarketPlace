package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.ClaimSyncWebhookResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceiveClaimWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 반품 철회 웹훅 Controller.
 *
 * <p>구매자가 반품 요청을 취소. RefundClaimSyncHandler에서 REFUND_REJECTED로 처리합니다.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
@Tag(name = "내부 웹훅", description = "자사몰 내부 웹훅 수신 API")
@RestController
public class ReturnWithdrawnWebhookController {

    private final ReceiveClaimWebhookUseCase receiveClaimWebhookUseCase;
    private final InternalWebhookApiMapper mapper;

    public ReturnWithdrawnWebhookController(
            ReceiveClaimWebhookUseCase receiveClaimWebhookUseCase,
            InternalWebhookApiMapper mapper) {
        this.receiveClaimWebhookUseCase = receiveClaimWebhookUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "반품 철회 웹훅", description = "구매자 반품 요청 취소를 처리합니다.")
    @PostMapping(InternalWebhookEndpoints.RETURN_WITHDRAWN)
    public ResponseEntity<ApiResponse<ClaimSyncWebhookResponse>> handleReturnWithdrawn(
            @RequestBody @Valid ReturnWithdrawnWebhookRequest request) {
        List<ExternalClaimPayload> payloads = mapper.toExternalClaimPayloads(request);
        ClaimSyncResult result =
                receiveClaimWebhookUseCase.execute(payloads, request.salesChannelId());
        return ResponseEntity.ok(ApiResponse.of(ClaimSyncWebhookResponse.from(result)));
    }
}
