package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceClaimClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceServerException;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelClaimSyncStrategy;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 취소 클레임 동기화 전략.
 *
 * <p>취소 Outbox 유형에 따라 세토프 Admin API v2를 호출합니다.
 *
 * <ul>
 *   <li>SELLER_CANCEL, APPROVE: POST /api/v2/cancels/{cancelId}/approve
 *   <li>REJECT: POST /api/v2/cancels/{cancelId}/reject + rejectReason
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofCancelClaimSyncStrategy implements CancelClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofCancelClaimSyncStrategy.class);

    private final SetofCommerceClaimClientAdapter claimClient;
    private final ObjectMapper objectMapper;

    public SetofCancelClaimSyncStrategy(
            SetofCommerceClaimClientAdapter claimClient, ObjectMapper objectMapper) {
        this.claimClient = claimClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxSyncResult execute(CancelOutbox outbox, Shop shop) {
        CancelOutboxType type = outbox.outboxType();

        try {
            String cancelId = extractCancelId(outbox.payload());

            switch (type) {
                case SELLER_CANCEL, APPROVE -> claimClient.approveCancel(shop, cancelId);
                case REJECT -> {
                    String reason = extractRejectReason(outbox.payload());
                    claimClient.rejectCancel(shop, cancelId, reason);
                }
            }

            return OutboxSyncResult.success();

        } catch (SetofCommerceBadRequestException | SetofCommerceClientException e) {
            log.warn(
                    "세토프 취소 동기화 실패 (재시도 불가): orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage());
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (SetofCommerceServerException e) {
            log.warn(
                    "세토프 취소 동기화 실패 (재시도 가능): orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage());
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (Exception e) {
            log.error(
                    "세토프 취소 동기화 중 예외: orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage(),
                    e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }

    private String extractCancelId(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.get("cancelId").asText();
        } catch (Exception e) {
            throw new IllegalStateException("cancelId를 payload에서 추출할 수 없습니다: " + payload, e);
        }
    }

    private String extractRejectReason(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.has("rejectReason") ? node.get("rejectReason").asText() : "거부 사유 없음";
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return "거부 사유 없음";
        }
    }
}
