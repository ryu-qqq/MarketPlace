package com.ryuqq.marketplace.adapter.out.client.naver.strategy;

import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceReturnClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRejectRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.refund.port.out.client.RefundClaimSyncStrategy;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 환불(반품) 클레임 동기화 전략.
 *
 * <p>환불 Outbox 유형에 따라 네이버 반품 API를 호출합니다.
 *
 * <ul>
 *   <li>REQUEST: 반품 요청
 *   <li>APPROVE: 반품 승인 (수거 시작)
 *   <li>REJECT: 반품 거절
 *   <li>COMPLETE: 환불 완료 (네이버 자동 처리, 성공으로 처리)
 * </ul>
 */
@Component
@ConditionalOnBean(NaverCommerceReturnClientAdapter.class)
public class NaverRefundClaimSyncStrategy implements RefundClaimSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverRefundClaimSyncStrategy.class);

    private final NaverCommerceReturnClientAdapter returnClient;
    private final ExternalOrderItemMappingQueryPort mappingQueryPort;

    public NaverRefundClaimSyncStrategy(
            NaverCommerceReturnClientAdapter returnClient,
            ExternalOrderItemMappingQueryPort mappingQueryPort) {
        this.returnClient = returnClient;
        this.mappingQueryPort = mappingQueryPort;
    }

    @Override
    public OutboxSyncResult execute(RefundOutbox outbox) {
        String externalProductOrderId = resolveExternalProductOrderId(outbox.orderItemIdValue());
        RefundOutboxType type = outbox.outboxType();

        try {
            NaverClaimResponse response =
                    switch (type) {
                        case REQUEST -> {
                            NaverReturnRequest request =
                                    new NaverReturnRequest(
                                            "INTENT_CHANGED", null, null, null, null);
                            yield returnClient.requestReturn(externalProductOrderId, request);
                        }
                        case APPROVE -> returnClient.approveReturn(externalProductOrderId);
                        case REJECT -> {
                            NaverReturnRejectRequest request =
                                    new NaverReturnRejectRequest("판매자 거절");
                            yield returnClient.rejectReturn(externalProductOrderId, request);
                        }
                        case COLLECT -> {
                            // 수거 완료는 내부 상태 변경만 수행 → 성공으로 처리
                            log.info(
                                    "환불 수거 완료 - 네이버 API 호출 없이 완료: orderItemId={}",
                                    outbox.orderItemIdValue());
                            yield null;
                        }
                        case COMPLETE -> {
                            // 환불 완료는 네이버 자동 처리 → 성공으로 처리
                            log.info(
                                    "환불 완료 - 네이버 API 호출 없이 완료: orderItemId={}",
                                    outbox.orderItemIdValue());
                            yield null;
                        }
                        case HOLD -> {
                            // holdbackReturn은 void 반환 → null 처리 → success
                            returnClient.holdbackReturn(externalProductOrderId);
                            yield null;
                        }
                        case RELEASE_HOLD -> {
                            // releaseReturnHoldback은 void 반환 → null 처리 → success
                            returnClient.releaseReturnHoldback(externalProductOrderId);
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
                    "환불 클레임 동기화 중 예외: orderItemId={}, error={}",
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
