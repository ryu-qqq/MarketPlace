package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.manager.ClaimSyncLogCommandManager;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.claimsync.aggregate.ClaimSyncLog;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncOutcome;
import com.ryuqq.marketplace.domain.claimsync.vo.ExternalClaimType;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 클레임 동기화 단건 처리 컴포넌트.
 *
 * <p>매핑이 확인된 클레임에 대해 다음 단계를 처리합니다:
 *
 * <ol>
 *   <li>외부 클레임 유형 → 내부 유형 변환 (ExternalClaimType 도메인 VO 사용)
 *   <li>InternalClaimType 기반으로 적절한 ClaimSyncHandler 라우팅
 *   <li>핸들러를 통한 액션 결정 및 실행
 *   <li>동기화 로그 기록
 * </ol>
 */
@Component
public class ClaimSyncProcessor {

    private static final Logger log = LoggerFactory.getLogger(ClaimSyncProcessor.class);

    private final Map<InternalClaimType, ClaimSyncHandler> handlers;
    private final OrderItemReadManager orderItemReadManager;
    private final OrderItemCommandManager orderItemCommandManager;
    private final ClaimSyncLogCommandManager syncLogCommandManager;
    private final TimeProvider timeProvider;

    public ClaimSyncProcessor(
            List<ClaimSyncHandler> handlerList,
            OrderItemReadManager orderItemReadManager,
            OrderItemCommandManager orderItemCommandManager,
            ClaimSyncLogCommandManager syncLogCommandManager,
            TimeProvider timeProvider) {
        this.handlers =
                handlerList.stream()
                        .collect(Collectors.toMap(ClaimSyncHandler::supportedType, h -> h));
        this.orderItemReadManager = orderItemReadManager;
        this.orderItemCommandManager = orderItemCommandManager;
        this.syncLogCommandManager = syncLogCommandManager;
        this.timeProvider = timeProvider;
    }

    /**
     * 단건 클레임을 동기화합니다.
     *
     * @param claim 외부 클레임 페이로드
     * @param mapping 검증 완료된 외부 주문상품 매핑
     * @param salesChannelId 판매채널 ID
     * @return 처리 결과
     */
    public ClaimSyncOutcome process(
            ExternalClaimPayload claim, ExternalOrderItemMapping mapping, long salesChannelId) {

        OrderItemId orderItemId = mapping.orderItemId();
        ExternalClaimType externalType = ExternalClaimType.fromString(claim.claimType());
        InternalClaimType internalType = externalType.toInternalType();

        ClaimSyncHandler handler = handlers.get(internalType);
        if (handler == null) {
            log.debug("미지원 클레임 타입 - 스킵: internalType={}", internalType);
            return ClaimSyncOutcome.SKIPPED;
        }

        ClaimSyncAction action = handler.resolve(claim, orderItemId);
        if (action == ClaimSyncAction.SKIPPED) {
            log.debug(
                    "액션 없음 - 스킵: externalProductOrderId={}, claimType={}, claimStatus={}",
                    claim.externalProductOrderId(),
                    claim.claimType(),
                    claim.claimStatus());
            return ClaimSyncOutcome.SKIPPED;
        }

        long sellerId = resolveSellerId(orderItemId);

        long internalClaimId = handler.execute(action, claim, orderItemId, sellerId);

        updateExternalOrderStatus(claim, orderItemId);

        recordSyncLog(claim, salesChannelId, internalType, internalClaimId, action);

        log.info(
                "클레임 동기화 완료: externalProductOrderId={}, action={}, internalClaimType={}",
                claim.externalProductOrderId(),
                action,
                internalType);

        return ClaimSyncOutcome.fromInternalType(internalType);
    }

    private void updateExternalOrderStatus(ExternalClaimPayload claim, OrderItemId orderItemId) {
        if (claim.productOrderStatus() == null || claim.productOrderStatus().isBlank()) {
            return;
        }
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            item.updateExternalOrderStatus(claim.productOrderStatus());
                            orderItemCommandManager.persistAll(List.of(item));
                        });
    }

    private long resolveSellerId(OrderItemId orderItemId) {
        Optional<OrderItem> orderItem = orderItemReadManager.findById(orderItemId);
        if (orderItem.isEmpty()) {
            throw new IllegalStateException(
                    "sellerId 조회 실패: OrderItem 없음. orderItemId=" + orderItemId.value());
        }
        Long sellerId = orderItem.get().internalProduct().sellerId();
        if (sellerId == null) {
            throw new IllegalStateException("sellerId가 null. orderItemId=" + orderItemId.value());
        }
        return sellerId;
    }

    private void recordSyncLog(
            ExternalClaimPayload claim,
            long salesChannelId,
            InternalClaimType internalType,
            long internalClaimId,
            ClaimSyncAction action) {
        ClaimSyncLog syncLog =
                ClaimSyncLog.forNew(
                        salesChannelId,
                        claim.externalProductOrderId(),
                        claim.claimType(),
                        claim.claimStatus(),
                        internalType.name(),
                        internalClaimId,
                        action,
                        timeProvider.now());
        syncLogCommandManager.record(syncLog);
    }
}
