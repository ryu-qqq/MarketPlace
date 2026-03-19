package com.ryuqq.marketplace.application.claimsync;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.ordermapping.id.ExternalOrderItemMappingId;
import java.time.Instant;

/**
 * ClaimSync Application 테스트 Fixtures.
 *
 * <p>ClaimSync 관련 DTO 및 도메인 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ClaimSyncFixtures {

    private ClaimSyncFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_EXTERNAL_ORDER_ID = "EXT-ORD-20260218-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ORDER_ID = "EXT-PO-20260218-001";
    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;

    // ===== ExternalClaimPayload - CANCEL =====

    public static ExternalClaimPayload cancelRequestPayload() {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "CANCEL",
                "CANCEL_REQUEST",
                "CLAIM-001",
                "CANCEL_REQUEST",
                "CHANGE_OF_MIND",
                null,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload cancelPayload(String claimStatus) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "CANCEL",
                claimStatus,
                "CLAIM-001",
                claimStatus,
                "CHANGE_OF_MIND",
                null,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload adminCancelPayload(String claimStatus) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "ADMIN_CANCEL",
                claimStatus,
                "CLAIM-002",
                claimStatus,
                "OUT_OF_STOCK",
                null,
                1,
                "SELLER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload cancelPayloadWithReason(
            String claimReason, String detailedReason) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "CANCEL",
                "CANCEL_REQUEST",
                "CLAIM-001",
                "CANCEL_REQUEST",
                claimReason,
                detailedReason,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    // ===== ExternalClaimPayload - RETURN (환불) =====

    public static ExternalClaimPayload returnRequestPayload() {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "RETURN",
                "RETURN_REQUEST",
                "CLAIM-003",
                "RETURN_REQUEST",
                "CHANGE_OF_MIND",
                null,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload returnPayload(String claimStatus) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "RETURN",
                claimStatus,
                "CLAIM-003",
                claimStatus,
                "CHANGE_OF_MIND",
                null,
                1,
                "BUYER",
                "CJ대한통운",
                "1234567890",
                "COLLECTING",
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload returnPayloadWithReason(String claimReason) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "RETURN",
                "RETURN_REQUEST",
                "CLAIM-003",
                "RETURN_REQUEST",
                claimReason,
                null,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    // ===== ExternalClaimPayload - EXCHANGE =====

    public static ExternalClaimPayload exchangeRequestPayload() {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "EXCHANGE",
                "EXCHANGE_REQUEST",
                "CLAIM-004",
                "EXCHANGE_REQUEST",
                "SIZE_CHANGE",
                "사이즈가 맞지 않아 교환 요청합니다",
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload exchangePayload(String claimStatus) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "EXCHANGE",
                claimStatus,
                "CLAIM-004",
                claimStatus,
                "SIZE_CHANGE",
                "사이즈가 맞지 않아 교환 요청합니다",
                1,
                "BUYER",
                "CJ대한통운",
                "9876543210",
                "COLLECTING",
                "한진택배",
                "1111111111",
                "SHIPPING",
                Instant.now(),
                Instant.now());
    }

    public static ExternalClaimPayload exchangePayloadWithReason(
            String claimReason, String detailedReason) {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "EXCHANGE",
                "EXCHANGE_REQUEST",
                "CLAIM-004",
                "EXCHANGE_REQUEST",
                claimReason,
                detailedReason,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    // ===== ExternalClaimPayload - 알 수 없는 유형 =====

    public static ExternalClaimPayload unknownClaimTypePayload() {
        return new ExternalClaimPayload(
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                "UNKNOWN_TYPE",
                "SOME_STATUS",
                "CLAIM-999",
                "SOME_STATUS",
                null,
                null,
                1,
                "BUYER",
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now());
    }

    // ===== ExternalOrderItemMapping =====

    public static ExternalOrderItemMapping defaultMapping() {
        return ExternalOrderItemMapping.reconstitute(
                ExternalOrderItemMappingId.of(1L),
                DEFAULT_SALES_CHANNEL_ID,
                "NAVER",
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                Instant.now());
    }

    public static ExternalOrderItemMapping mappingWithOrderItemId(String orderItemId) {
        return ExternalOrderItemMapping.reconstitute(
                ExternalOrderItemMappingId.of(1L),
                DEFAULT_SALES_CHANNEL_ID,
                "NAVER",
                DEFAULT_EXTERNAL_ORDER_ID,
                DEFAULT_EXTERNAL_PRODUCT_ORDER_ID,
                OrderItemId.of(orderItemId),
                Instant.now());
    }

    // ===== ClaimSyncResult =====

    public static ClaimSyncResult emptyResult() {
        return ClaimSyncResult.empty();
    }

    public static ClaimSyncResult resultWithCancel(int total, int cancelSynced) {
        return new ClaimSyncResult(total, cancelSynced, 0, 0, 0, 0);
    }

    public static ClaimSyncResult resultWithRefund(int total, int refundSynced) {
        return new ClaimSyncResult(total, 0, refundSynced, 0, 0, 0);
    }

    public static ClaimSyncResult resultWithExchange(int total, int exchangeSynced) {
        return new ClaimSyncResult(total, 0, 0, exchangeSynced, 0, 0);
    }

    public static ClaimSyncResult fullResult(
            int total, int cancel, int refund, int exchange, int skipped, int failed) {
        return new ClaimSyncResult(total, cancel, refund, exchange, skipped, failed);
    }
}
