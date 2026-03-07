package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;

/**
 * 외부 채널 연동 실행 전략 인터페이스.
 *
 * <p>채널별(네이버, 세토프 등) + SyncType별(CREATE, UPDATE, DELETE) 구현체가 존재합니다.
 */
public interface OutboundSyncExecutionStrategy {

    /**
     * 이 전략이 주어진 채널 코드와 SyncType을 지원하는지 확인합니다.
     *
     * @param channelCode 채널 코드 (예: "NAVER", "SETOF")
     * @param syncType 연동 타입
     * @return 지원 여부
     */
    boolean supports(String channelCode, SyncType syncType);

    /**
     * 외부 채널 연동을 실행합니다.
     *
     * @param context 실행 컨텍스트 (outbox, 채널 정보, 상품그룹 ID 등)
     * @return 실행 결과 (성공/실패, 외부 상품 ID, 에러 메시지)
     */
    OutboundSyncExecutionResult execute(OutboundSyncExecutionContext context);
}
