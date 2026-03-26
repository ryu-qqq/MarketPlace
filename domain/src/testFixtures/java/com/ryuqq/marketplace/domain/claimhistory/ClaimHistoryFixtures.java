package com.ryuqq.marketplace.domain.claimhistory;

import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.id.ClaimHistoryId;
import com.ryuqq.marketplace.domain.claimhistory.vo.Actor;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;

/**
 * ClaimHistory 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ClaimHistory 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ClaimHistoryFixtures {

    private ClaimHistoryFixtures() {}

    public static final String DEFAULT_ORDER_ITEM_ID = "order-item-001";

    // ===== ClaimHistoryId Fixtures =====

    public static ClaimHistoryId defaultClaimHistoryId() {
        return ClaimHistoryId.of("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70");
    }

    public static ClaimHistoryId claimHistoryId(String value) {
        return ClaimHistoryId.of(value);
    }

    public static ClaimHistoryId generatedClaimHistoryId() {
        return ClaimHistoryId.generate();
    }

    // ===== Actor Fixtures =====

    public static Actor systemActor() {
        return Actor.system();
    }

    public static Actor adminActor() {
        return Actor.admin("admin-001", "관리자");
    }

    public static Actor sellerActor() {
        return Actor.seller("seller-001", "판매자");
    }

    public static Actor customerActor() {
        return Actor.customer("customer-001", "고객");
    }

    // ===== ClaimHistory Aggregate Fixtures =====

    public static ClaimHistory statusChangeClaimHistory() {
        return ClaimHistory.forStatusChange(
                defaultClaimHistoryId(),
                ClaimType.CANCEL,
                "cancel-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                "REQUESTED",
                "APPROVED",
                systemActor(),
                CommonVoFixtures.now());
    }

    public static ClaimHistory manualClaimHistory() {
        return ClaimHistory.forManual(
                defaultClaimHistoryId(),
                ClaimType.REFUND,
                "refund-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                "CS 담당자 확인 완료",
                adminActor(),
                CommonVoFixtures.now());
    }

    public static ClaimHistory cancelStatusChangeHistory() {
        return ClaimHistory.forStatusChange(
                defaultClaimHistoryId(),
                ClaimType.CANCEL,
                "cancel-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                "REQUESTED",
                "APPROVED",
                systemActor(),
                CommonVoFixtures.now());
    }

    public static ClaimHistory refundStatusChangeHistory() {
        return ClaimHistory.forStatusChange(
                claimHistoryId("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f71"),
                ClaimType.REFUND,
                "refund-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                "APPROVED",
                "COMPLETED",
                systemActor(),
                CommonVoFixtures.now());
    }

    public static ClaimHistory exchangeStatusChangeHistory() {
        return ClaimHistory.forStatusChange(
                claimHistoryId("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f72"),
                ClaimType.EXCHANGE,
                "exchange-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                "COLLECTING",
                "COLLECTED",
                systemActor(),
                CommonVoFixtures.now());
    }

    public static ClaimHistory orderManualClaimHistory() {
        return ClaimHistory.forManual(
                generatedClaimHistoryId(),
                ClaimType.ORDER,
                null,
                DEFAULT_ORDER_ITEM_ID,
                "일반 주문 메모",
                adminActor(),
                CommonVoFixtures.now());
    }

    public static ClaimHistory reconstitutedClaimHistory() {
        Instant createdAt = CommonVoFixtures.yesterday();
        return ClaimHistory.reconstitute(
                defaultClaimHistoryId(),
                ClaimType.CANCEL,
                "cancel-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                ClaimHistoryType.STATUS_CHANGE,
                "승인",
                "REQUESTED → APPROVED",
                systemActor(),
                createdAt);
    }

    public static ClaimHistory reconstitutedManualClaimHistory() {
        Instant createdAt = CommonVoFixtures.yesterday();
        return ClaimHistory.reconstitute(
                claimHistoryId("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f73"),
                ClaimType.REFUND,
                "refund-claim-001",
                DEFAULT_ORDER_ITEM_ID,
                ClaimHistoryType.MANUAL,
                "CS 메모",
                "고객 요청으로 환불 처리",
                adminActor(),
                createdAt);
    }
}
