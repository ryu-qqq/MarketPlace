package com.ryuqq.marketplace.adapter.out.client.naver.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceCancelClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverCancelRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelClaimSyncStrategy;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 취소 클레임 동기화 전략.
 *
 * <p>취소 Outbox 유형에 따라 네이버 취소 API를 호출합니다.
 *
 * <ul>
 *   <li>SELLER_CANCEL: 판매자 취소 요청
 *   <li>APPROVE: 구매자 취소 승인
 *   <li>REJECT: 취소 거절 (네이버 API 없음, 성공으로 처리)
 * </ul>
 */
@Component
@ConditionalOnBean(NaverCommerceCancelClientAdapter.class)
public class NaverCancelClaimSyncStrategy implements CancelClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverCancelClaimSyncStrategy.class);

    private final NaverCommerceCancelClientAdapter cancelClient;
    private final ExternalOrderItemMappingQueryPort mappingQueryPort;
    private final ObjectMapper objectMapper;

    public NaverCancelClaimSyncStrategy(
            NaverCommerceCancelClientAdapter cancelClient,
            ExternalOrderItemMappingQueryPort mappingQueryPort,
            ObjectMapper objectMapper) {
        this.cancelClient = cancelClient;
        this.mappingQueryPort = mappingQueryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxSyncResult execute(CancelOutbox outbox) {
        String externalProductOrderId = resolveExternalProductOrderId(outbox.orderItemIdValue());
        CancelOutboxType type = outbox.outboxType();

        try {
            NaverClaimResponse response =
                    switch (type) {
                        case SELLER_CANCEL -> {
                            JsonNode payload = objectMapper.readTree(outbox.payload());
                            String cancelDetailedReason =
                                    payload.has("cancelDetailedReason")
                                            ? payload.get("cancelDetailedReason").asText()
                                            : null;
                            Integer cancelQty =
                                    payload.has("cancelQty")
                                            ? payload.get("cancelQty").asInt()
                                            : null;
                            NaverCancelRequest request =
                                    new NaverCancelRequest(
                                            "INTENT_CHANGED", cancelDetailedReason, cancelQty);
                            yield cancelClient.requestCancel(externalProductOrderId, request);
                        }
                        case APPROVE -> cancelClient.approveCancel(externalProductOrderId);
                        case REJECT -> {
                            // 네이버 취소 거절 API 없음 → 성공으로 처리
                            log.info(
                                    "취소 거절 - 네이버 API 호출 없이 완료: orderItemId={}",
                                    outbox.orderItemIdValue());
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
                    "취소 클레임 동기화 중 예외: orderItemId={}, error={}",
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
