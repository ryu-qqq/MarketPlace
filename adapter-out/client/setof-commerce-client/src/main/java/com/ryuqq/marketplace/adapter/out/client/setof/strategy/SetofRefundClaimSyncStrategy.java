package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceClaimClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceServerException;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.refund.port.out.client.RefundClaimSyncStrategy;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 환불(반품) 클레임 동기화 전략.
 *
 * <p>환불 Outbox 유형에 따라 세토프 Admin API v2를 호출합니다.
 *
 * <ul>
 *   <li>COMPLETE: POST /api/v2/refunds/{refundId}/complete
 *   <li>REJECT: POST /api/v2/refunds/{refundId}/reject + rejectReason
 *   <li>REQUEST, APPROVE, COLLECT: 내부 상태 변경만 → 성공 처리
 *   <li>HOLD, RELEASE_HOLD: 세토프 미지원 → 성공 처리
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofRefundClaimSyncStrategy implements RefundClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofRefundClaimSyncStrategy.class);

    private final SetofCommerceClaimClientAdapter claimClient;
    private final ObjectMapper objectMapper;

    public SetofRefundClaimSyncStrategy(
            SetofCommerceClaimClientAdapter claimClient, ObjectMapper objectMapper) {
        this.claimClient = claimClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public OutboxSyncResult execute(RefundOutbox outbox) {
        RefundOutboxType type = outbox.outboxType();

        try {
            switch (type) {
                case COMPLETE -> {
                    String refundClaimId = extractRefundClaimId(outbox.payload());
                    claimClient.completeRefund(refundClaimId);
                }
                case REJECT -> {
                    String refundClaimId = extractRefundClaimId(outbox.payload());
                    String reason = extractRejectReason(outbox.payload());
                    claimClient.rejectRefund(refundClaimId, reason);
                }
                case REQUEST, APPROVE, COLLECT -> {
                    log.info("세토프 환불 {} - 내부 처리만: orderItemId={}", type, outbox.orderItemIdValue());
                }
                case HOLD, RELEASE_HOLD -> {
                    log.info(
                            "세토프 환불 {} - 미지원 기능, 스킵: orderItemId={}",
                            type,
                            outbox.orderItemIdValue());
                }
            }

            return OutboxSyncResult.success();

        } catch (SetofCommerceBadRequestException | SetofCommerceClientException e) {
            log.warn(
                    "세토프 환불 동기화 실패 (재시도 불가): orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage());
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (SetofCommerceServerException e) {
            log.warn(
                    "세토프 환불 동기화 실패 (재시도 가능): orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage());
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (Exception e) {
            log.error(
                    "세토프 환불 동기화 중 예외: orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage(),
                    e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }

    private String extractRefundClaimId(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.get("refundClaimId").asText();
        } catch (Exception e) {
            throw new IllegalStateException("refundClaimId를 payload에서 추출할 수 없습니다: " + payload, e);
        }
    }

    private String extractRejectReason(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.has("rejectReason") ? node.get("rejectReason").asText() : "거부 사유 없음";
        } catch (Exception e) {
            return "거부 사유 없음";
        }
    }
}
