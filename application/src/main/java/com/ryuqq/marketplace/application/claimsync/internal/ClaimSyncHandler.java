package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;

/**
 * 특정 클레임 유형의 동기화 처리를 담당하는 핸들러 인터페이스.
 *
 * <p>각 구현체는 자신이 처리하는 InternalClaimType을 선언하고, 액션 결정(resolve)과 실행(execute)을 완전히 캡슐화합니다.
 */
public interface ClaimSyncHandler {

    /**
     * 이 핸들러가 처리하는 내부 클레임 유형을 반환합니다.
     *
     * @return 지원하는 InternalClaimType
     */
    InternalClaimType supportedType();

    /**
     * 외부 클레임 페이로드와 기존 내부 클레임 상태를 분석하여 수행할 액션을 결정합니다.
     *
     * @param claim 외부몰에서 수신한 클레임 데이터
     * @param orderItemId 내부 주문상품 ID
     * @return 수행할 ClaimSyncAction
     */
    ClaimSyncAction resolve(ExternalClaimPayload claim, OrderItemId orderItemId);

    /**
     * 결정된 액션을 실행하여 내부 도메인 상태를 변경하고 영속화합니다.
     *
     * @param action 수행할 클레임 동기화 액션
     * @param claim 외부몰에서 수신한 클레임 데이터
     * @param orderItemId 내부 주문상품 ID
     * @param sellerId 판매자 ID
     * @return 내부 클레임 식별자 (로그 기록용)
     */
    long execute(
            ClaimSyncAction action,
            ExternalClaimPayload claim,
            OrderItemId orderItemId,
            long sellerId);
}
