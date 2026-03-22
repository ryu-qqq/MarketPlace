package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.PurchaseConfirmedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.PurchaseConfirmedWebhookRequest.PurchaseConfirmedItemRequest;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceivePurchaseConfirmedWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 구매 확정 웹훅 Controller.
 *
 * <p>배송 완료 후 자동/수동 구매 확정. 이미 확정된 항목은 무시합니다.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
@Tag(name = "내부 웹훅", description = "자사몰 내부 웹훅 수신 API")
@RestController
public class PurchaseConfirmedWebhookController {

    private final ReceivePurchaseConfirmedWebhookUseCase receivePurchaseConfirmedWebhookUseCase;

    public PurchaseConfirmedWebhookController(
            ReceivePurchaseConfirmedWebhookUseCase receivePurchaseConfirmedWebhookUseCase) {
        this.receivePurchaseConfirmedWebhookUseCase = receivePurchaseConfirmedWebhookUseCase;
    }

    @Operation(summary = "구매 확정 웹훅", description = "구매 확정 이벤트를 처리합니다.")
    @PostMapping(InternalWebhookEndpoints.PURCHASE_CONFIRMED)
    public ResponseEntity<ApiResponse<Void>> handlePurchaseConfirmed(
            @RequestBody @Valid PurchaseConfirmedWebhookRequest request) {
        List<String> externalProductOrderIds =
                request.items().stream()
                        .map(PurchaseConfirmedItemRequest::externalProductOrderId)
                        .toList();
        receivePurchaseConfirmedWebhookUseCase.execute(
                request.salesChannelId(), externalProductOrderIds);
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
