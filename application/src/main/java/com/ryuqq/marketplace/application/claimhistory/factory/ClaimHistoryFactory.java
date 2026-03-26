package com.ryuqq.marketplace.application.claimhistory.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.id.ClaimHistoryId;
import com.ryuqq.marketplace.domain.claimhistory.vo.Actor;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.springframework.stereotype.Component;

/** 클레임 이력 도메인 객체 생성 팩토리. */
@Component
public class ClaimHistoryFactory {

    private final TimeProvider timeProvider;

    public ClaimHistoryFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 상태 변경 이력 생성. */
    public ClaimHistory createStatusChange(
            ClaimType claimType,
            String claimId,
            String orderItemId,
            String fromStatus,
            String toStatus,
            String actorId,
            String actorName) {
        return ClaimHistory.forStatusChange(
                ClaimHistoryId.generate(),
                claimType,
                claimId,
                orderItemId,
                fromStatus,
                toStatus,
                Actor.admin(actorId, actorName),
                timeProvider.now());
    }

    /** 시스템에 의한 상태 변경 이력 생성. */
    public ClaimHistory createStatusChangeBySystem(
            ClaimType claimType,
            String claimId,
            String orderItemId,
            String fromStatus,
            String toStatus) {
        return ClaimHistory.forStatusChange(
                ClaimHistoryId.generate(),
                claimType,
                claimId,
                orderItemId,
                fromStatus,
                toStatus,
                Actor.system(),
                timeProvider.now());
    }

    /** 시스템에 의한 상태 변경 이력 생성 (수량 정보 포함). */
    public ClaimHistory createStatusChangeBySystemWithQty(
            ClaimType claimType,
            String claimId,
            String orderItemId,
            String fromStatus,
            String toStatus,
            int quantity) {
        return ClaimHistory.forStatusChangeWithQty(
                ClaimHistoryId.generate(),
                claimType,
                claimId,
                orderItemId,
                fromStatus,
                toStatus,
                quantity,
                Actor.system(),
                timeProvider.now());
    }

    /** 수기 메모 이력 생성. */
    public ClaimHistory createManualMemo(
            ClaimType claimType,
            String claimId,
            String orderItemId,
            String message,
            String actorId,
            String actorName) {
        return ClaimHistory.forManual(
                ClaimHistoryId.generate(),
                claimType,
                claimId,
                orderItemId,
                message,
                Actor.admin(actorId, actorName),
                timeProvider.now());
    }
}
