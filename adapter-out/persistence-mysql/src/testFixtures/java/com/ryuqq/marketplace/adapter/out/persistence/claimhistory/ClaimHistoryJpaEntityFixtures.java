package com.ryuqq.marketplace.adapter.out.persistence.claimhistory;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import java.time.Instant;
import java.util.UUID;

/**
 * ClaimHistoryJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ClaimHistoryJpaEntity 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ClaimHistoryJpaEntityFixtures {

    private ClaimHistoryJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70";
    public static final String DEFAULT_CLAIM_ID = "cancel-claim-001";
    public static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final String DEFAULT_CLAIM_TYPE = "CANCEL";
    public static final String DEFAULT_HISTORY_TYPE = "STATUS_CHANGE";
    public static final String DEFAULT_TITLE = "승인";
    public static final String DEFAULT_MESSAGE = "REQUESTED → APPROVED";
    public static final String DEFAULT_ACTOR_TYPE = "SYSTEM";
    public static final String DEFAULT_ACTOR_ID = "system";
    public static final String DEFAULT_ACTOR_NAME = "시스템";

    // ===== Entity Fixtures =====

    /** 기본 ClaimHistory Entity 생성. */
    public static ClaimHistoryJpaEntity defaultEntity() {
        return ClaimHistoryJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CLAIM_TYPE,
                DEFAULT_CLAIM_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_HISTORY_TYPE,
                DEFAULT_TITLE,
                DEFAULT_MESSAGE,
                DEFAULT_ACTOR_TYPE,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME,
                Instant.now());
    }

    /** ID를 지정한 Entity 생성. */
    public static ClaimHistoryJpaEntity entityWithId(String id) {
        return ClaimHistoryJpaEntity.create(
                id,
                DEFAULT_CLAIM_TYPE,
                DEFAULT_CLAIM_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_HISTORY_TYPE,
                DEFAULT_TITLE,
                DEFAULT_MESSAGE,
                DEFAULT_ACTOR_TYPE,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME,
                Instant.now());
    }

    /** CANCEL 타입의 상태 변경 Entity 생성. */
    public static ClaimHistoryJpaEntity cancelStatusChangeEntity(String claimId) {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                "CANCEL",
                claimId,
                DEFAULT_ORDER_ITEM_ID,
                "STATUS_CHANGE",
                "승인",
                "REQUESTED → APPROVED",
                "SYSTEM",
                "system",
                "시스템",
                Instant.now());
    }

    /** REFUND 타입의 상태 변경 Entity 생성. */
    public static ClaimHistoryJpaEntity refundStatusChangeEntity(String claimId) {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                "REFUND",
                claimId,
                DEFAULT_ORDER_ITEM_ID,
                "STATUS_CHANGE",
                "완료",
                "APPROVED → COMPLETED",
                "SYSTEM",
                "system",
                "시스템",
                Instant.now());
    }

    /** EXCHANGE 타입의 상태 변경 Entity 생성. */
    public static ClaimHistoryJpaEntity exchangeStatusChangeEntity(String claimId) {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                "EXCHANGE",
                claimId,
                DEFAULT_ORDER_ITEM_ID,
                "STATUS_CHANGE",
                "수거 완료",
                "COLLECTING → COLLECTED",
                "SYSTEM",
                "system",
                "시스템",
                Instant.now());
    }

    /** 수기 메모 Entity 생성 (ADMIN actor). */
    public static ClaimHistoryJpaEntity manualMemoEntity(String claimId) {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                "CANCEL",
                claimId,
                DEFAULT_ORDER_ITEM_ID,
                "MANUAL",
                "CS 메모",
                "고객 요청으로 처리",
                "ADMIN",
                "admin-001",
                "관리자",
                Instant.now());
    }

    /** ORDER 타입 수기 메모 Entity 생성 (claimId null). */
    public static ClaimHistoryJpaEntity orderMemoEntity(Long orderItemId) {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                "ORDER",
                null,
                orderItemId,
                "MANUAL",
                "CS 메모",
                "일반 주문 메모",
                "ADMIN",
                "admin-001",
                "관리자",
                Instant.now());
    }

    /** 특정 claimType + claimId로 Entity 생성. */
    public static ClaimHistoryJpaEntity entityWith(String claimType, String claimId) {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                claimType,
                claimId,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_HISTORY_TYPE,
                DEFAULT_TITLE,
                DEFAULT_MESSAGE,
                DEFAULT_ACTOR_TYPE,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME,
                Instant.now());
    }

    /** 새 UUID로 Entity 생성 (uniqueness 보장). */
    public static ClaimHistoryJpaEntity newEntity() {
        return ClaimHistoryJpaEntity.create(
                UUID.randomUUID().toString(),
                DEFAULT_CLAIM_TYPE,
                DEFAULT_CLAIM_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_HISTORY_TYPE,
                DEFAULT_TITLE,
                DEFAULT_MESSAGE,
                DEFAULT_ACTOR_TYPE,
                DEFAULT_ACTOR_ID,
                DEFAULT_ACTOR_NAME,
                Instant.now());
    }
}
