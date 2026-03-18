package com.ryuqq.marketplace.adapter.out.client.naver.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceExchangeClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeReDeliveryRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeRejectRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeClaimSyncStrategy;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 교환 클레임 동기화 전략.
 *
 * <p>교환 Outbox 유형에 따라 네이버 교환 API를 호출합니다.
 *
 * <ul>
 *   <li>COLLECT: 수거완료 승인
 *   <li>SHIP: 재배송 (payload에서 deliveryCompany, trackingNumber 추출)
 *   <li>REJECT: 교환 거절
 * </ul>
 */
@Component
public class NaverExchangeClaimSyncStrategy implements ExchangeClaimSyncStrategy {

    private static final Logger log =
            LoggerFactory.getLogger(NaverExchangeClaimSyncStrategy.class);

    private final NaverCommerceExchangeClientAdapter exchangeClient;
    private final ExternalOrderItemMappingQueryPort mappingQueryPort;
    private final ObjectMapper objectMapper;

    public NaverExchangeClaimSyncStrategy(
            NaverCommerceExchangeClientAdapter exchangeClient,
            ExternalOrderItemMappingQueryPort mappingQueryPort,
            ObjectMapper objectMapper) {
        this.exchangeClient = exchangeClient;
        this.mappingQueryPort = mappingQueryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxSyncResult execute(ExchangeOutbox outbox) {
        String externalProductOrderId = resolveExternalProductOrderId(outbox.orderItemIdValue());
        ExchangeOutboxType type = outbox.outboxType();

        try {
            NaverClaimResponse response =
                    switch (type) {
                        case COLLECT ->
                                exchangeClient.approveCollectedExchange(externalProductOrderId);
                        case SHIP -> {
                            JsonNode payload = objectMapper.readTree(outbox.payload());
                            String deliveryCompany = payload.get("deliveryCompany").asText();
                            String trackingNumber = payload.get("trackingNumber").asText();
                            NaverExchangeReDeliveryRequest request =
                                    new NaverExchangeReDeliveryRequest(
                                            "DELIVERY", deliveryCompany, trackingNumber);
                            yield exchangeClient.reDeliverExchange(
                                    externalProductOrderId, request);
                        }
                        case REJECT -> {
                            NaverExchangeRejectRequest request =
                                    new NaverExchangeRejectRequest("판매자 거절");
                            yield exchangeClient.rejectExchange(externalProductOrderId, request);
                        }
                        case HOLD -> {
                            // holdbackExchange는 void 반환 → null 처리 → success
                            exchangeClient.holdbackExchange(externalProductOrderId);
                            yield null;
                        }
                        case RELEASE_HOLD -> {
                            // releaseExchangeHoldback은 void 반환 → null 처리 → success
                            exchangeClient.releaseExchangeHoldback(externalProductOrderId);
                            yield null;
                        }
                    };

            return toSyncResult(response);

        } catch (NaverCommerceBadRequestException | NaverCommerceClientException e) {
            // 4xx → 재시도 불가
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (NaverCommerceServerException | NaverCommerceRateLimitException e) {
            // 5xx, 429 → 재시도 가능
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (ExternalServiceUnavailableException e) {
            // CB OPEN → 상위로 전파
            throw e;
        } catch (Exception e) {
            log.error(
                    "교환 클레임 동기화 중 예외: orderItemId={}, error={}",
                    outbox.orderItemIdValue(),
                    e.getMessage(),
                    e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }

    private OutboxSyncResult toSyncResult(NaverClaimResponse response) {
        if (response == null) {
            return OutboxSyncResult.success();
        }
        if (response.data() == null) {
            return OutboxSyncResult.success();
        }
        if (response.data().failProductOrderInfos() != null
                && !response.data().failProductOrderInfos().isEmpty()) {
            String failMsg = response.data().failProductOrderInfos().get(0).message();
            return OutboxSyncResult.failure(false, failMsg);
        }
        return OutboxSyncResult.success();
    }

    private String resolveExternalProductOrderId(String orderItemId) {
        return mappingQueryPort
                .findByOrderItemId(orderItemId)
                .map(m -> m.externalProductOrderId())
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "외부 상품주문 매핑을 찾을 수 없습니다. orderItemId=" + orderItemId));
    }
}
