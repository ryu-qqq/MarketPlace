package com.ryuqq.marketplace.application.outboundsync.dto.vo;

import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;

/**
 * 외부 채널 연동 실행 컨텍스트.
 *
 * <p>Strategy에 필요한 모든 정보를 전달합니다.
 *
 * @param outbox 처리 대상 Outbox
 * @param sellerSalesChannel 셀러 판매채널 (채널 코드, 인증 정보)
 * @param productGroupId 상품그룹 ID
 * @param syncType 연동 타입
 */
public record OutboundSyncExecutionContext(
        OutboundSyncOutbox outbox,
        SellerSalesChannel sellerSalesChannel,
        Long productGroupId,
        SyncType syncType) {}
