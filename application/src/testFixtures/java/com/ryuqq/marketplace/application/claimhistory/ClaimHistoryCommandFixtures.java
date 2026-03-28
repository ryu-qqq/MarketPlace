package com.ryuqq.marketplace.application.claimhistory;

import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;

/**
 * ClaimHistory Command 테스트 Fixtures.
 *
 * <p>ClaimHistory 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ClaimHistoryCommandFixtures {

    private ClaimHistoryCommandFixtures() {}

    // ===== 기본 값 상수 =====
    public static final ClaimType DEFAULT_CLAIM_TYPE = ClaimType.CANCEL;
    public static final String DEFAULT_CLAIM_ID = "cancel-claim-001";
    public static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final String DEFAULT_MESSAGE = "고객 요청으로 취소 처리 확인";
    public static final String DEFAULT_ACTOR_ID = "admin-001";
    public static final String DEFAULT_ACTOR_NAME = "관리자";

    // ===== AddClaimHistoryMemoCommand =====

    public static AddClaimHistoryMemoCommand addMemoCommand() {
        return new AddClaimHistoryMemoCommand(
                DEFAULT_CLAIM_TYPE,
                DEFAULT_CLAIM_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_MESSAGE,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME);
    }

    public static AddClaimHistoryMemoCommand addMemoCommand(ClaimType claimType, String claimId) {
        return new AddClaimHistoryMemoCommand(
                claimType,
                claimId,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_MESSAGE,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME);
    }

    public static AddClaimHistoryMemoCommand addMemoCommand(String message) {
        return new AddClaimHistoryMemoCommand(
                DEFAULT_CLAIM_TYPE,
                DEFAULT_CLAIM_ID,
                DEFAULT_ORDER_ITEM_ID,
                message,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME);
    }

    public static AddClaimHistoryMemoCommand addMemoCommand(
            ClaimType claimType,
            String claimId,
            Long orderItemId,
            String message,
            String actorId,
            String actorName) {
        return new AddClaimHistoryMemoCommand(
                claimType, claimId, orderItemId, message, actorId, actorName);
    }

    public static AddClaimHistoryMemoCommand addOrderMemoCommand(Long orderItemId, String message) {
        return new AddClaimHistoryMemoCommand(
                ClaimType.ORDER, null, orderItemId, message, DEFAULT_ACTOR_ID, DEFAULT_ACTOR_NAME);
    }
}
