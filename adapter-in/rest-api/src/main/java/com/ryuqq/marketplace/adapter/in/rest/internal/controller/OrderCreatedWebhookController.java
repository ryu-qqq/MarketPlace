package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.OrderCreatedWebhookResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceiveOrderCreatedWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문 생성 웹훅 Controller.
 *
 * <p>자사몰 결제 완료 시 호출. InboundOrder 파이프라인으로 주문을 수신합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity + ApiResponse 래핑.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
@Tag(name = "내부 웹훅", description = "자사몰 내부 웹훅 수신 API")
@RestController
public class OrderCreatedWebhookController {

    private final ReceiveOrderCreatedWebhookUseCase receiveOrderCreatedWebhookUseCase;
    private final InternalWebhookApiMapper mapper;

    public OrderCreatedWebhookController(
            ReceiveOrderCreatedWebhookUseCase receiveOrderCreatedWebhookUseCase,
            InternalWebhookApiMapper mapper) {
        this.receiveOrderCreatedWebhookUseCase = receiveOrderCreatedWebhookUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "주문 생성 웹훅", description = "자사몰 결제 완료 시 주문을 수신합니다.")
    @PostMapping(InternalWebhookEndpoints.CREATED)
    public ResponseEntity<ApiResponse<OrderCreatedWebhookResponse>> handleOrderCreated(
            @RequestBody @Valid OrderCreatedWebhookRequest request) {
        ExternalOrderPayload payload = mapper.toExternalOrderPayload(request);
        InboundOrderPollingResult result =
                receiveOrderCreatedWebhookUseCase.execute(
                        List.of(payload), request.salesChannelId(), request.shopId());
        return ResponseEntity.ok(ApiResponse.of(OrderCreatedWebhookResponse.from(result)));
    }
}
