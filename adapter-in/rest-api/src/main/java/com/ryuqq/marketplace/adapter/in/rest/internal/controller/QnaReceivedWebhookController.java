package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.QnaReceivedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.QnaWebhookResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.ReceiveQnaWebhookUseCase;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * QnA 수신 웹훅 Controller.
 *
 * <p>자사몰 QnA 등록 시 호출. sellerId → shop.account_id 역조회 후 InboundQna 파이프라인으로 수신합니다.
 *
 * <p>sellerId에 해당하는 Shop이 없으면 400 Bad Request를 반환합니다.
 */
@Tag(name = "내부 웹훅", description = "자사몰 내부 웹훅 수신 API")
@RestController
public class QnaReceivedWebhookController {

    private final ReceiveQnaWebhookUseCase receiveQnaWebhookUseCase;
    private final InternalWebhookApiMapper mapper;
    private final ShopReadManager shopReadManager;

    public QnaReceivedWebhookController(
            ReceiveQnaWebhookUseCase receiveQnaWebhookUseCase,
            InternalWebhookApiMapper mapper,
            ShopReadManager shopReadManager) {
        this.receiveQnaWebhookUseCase = receiveQnaWebhookUseCase;
        this.mapper = mapper;
        this.shopReadManager = shopReadManager;
    }

    @Operation(summary = "QnA 수신 웹훅", description = "자사몰 QnA 등록 시 문의를 수신합니다.")
    @PostMapping(InternalWebhookEndpoints.QNA_RECEIVED)
    public ResponseEntity<ApiResponse<QnaWebhookResponse>> handleQnaReceived(
            @RequestBody @Valid QnaReceivedWebhookRequest request) {

        long salesChannelId = request.salesChannelId();

        // sellerId 중복 제거 후 일괄 검증
        Set<Long> sellerIds =
                request.qnas().stream()
                        .map(QnaReceivedWebhookRequest.QnaItemRequest::sellerId)
                        .collect(Collectors.toSet());

        // 각 sellerId에 대해 Shop 역조회 (실패 시 ShopNotFoundException → 400)
        for (Long sellerId : sellerIds) {
            shopReadManager.getBySalesChannelIdAndAccountId(
                    salesChannelId, String.valueOf(sellerId));
        }

        // Shop 역조회 시 첫 번째 Shop의 ID를 사용 (세토프는 salesChannelId 기준으로 동작)
        Shop firstShop =
                shopReadManager.getBySalesChannelIdAndAccountId(
                        salesChannelId,
                        String.valueOf(request.qnas().get(0).sellerId()));

        List<ExternalQnaPayload> payloads = mapper.toExternalQnaPayloads(request);
        QnaWebhookResult result =
                receiveQnaWebhookUseCase.execute(
                        payloads, salesChannelId, firstShop.idValue());

        return ResponseEntity.ok(ApiResponse.of(QnaWebhookResponse.from(result)));
    }
}
