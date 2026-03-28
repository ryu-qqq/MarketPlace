package com.ryuqq.marketplace.application.refund;

import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * Refund Application Query 테스트 Fixtures.
 *
 * <p>Refund 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class RefundQueryFixtures {

    private RefundQueryFixtures() {}

    private static final String DEFAULT_REFUND_CLAIM_ID = "01900000-0000-7000-8000-000000000010";
    private static final Long DEFAULT_ORDER_ITEM_ID = 1001L;

    // ===== RefundSearchParams Fixtures =====

    public static RefundSearchParams searchParams() {
        return new RefundSearchParams(
                null, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static RefundSearchParams searchParams(int page, int size) {
        return new RefundSearchParams(
                null, null, null, null, null, null, "CREATED_AT", "DESC", page, size);
    }

    public static RefundSearchParams searchParamsByStatus(String status) {
        return new RefundSearchParams(
                List.of(status), null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static RefundSearchParams searchParamsByDateRange(String startDate, String endDate) {
        return new RefundSearchParams(
                null, null, null, "REQUESTED", startDate, endDate, "CREATED_AT", "DESC", 0, 20);
    }

    // ===== RefundListResult Fixtures =====

    public static RefundListResult refundListResult() {
        return refundListResult(DEFAULT_REFUND_CLAIM_ID);
    }

    public static RefundListResult refundListResult(String refundClaimId) {
        return new RefundListResult(
                refundClaimId,
                "RFD-20260218-0001",
                DEFAULT_ORDER_ITEM_ID,
                1,
                "REQUESTED",
                "CHANGE_OF_MIND",
                "단순 변심입니다.",
                null,
                null,
                null,
                "customer@marketplace.com",
                null,
                Instant.now(),
                null,
                null);
    }

    // ===== RefundDetailResult Fixtures =====

    public static RefundDetailResult refundDetailResult() {
        return refundDetailResult(DEFAULT_REFUND_CLAIM_ID);
    }

    public static RefundDetailResult refundDetailResult(String refundClaimId) {
        return new RefundDetailResult(
                refundClaimId,
                "RFD-20260218-0001",
                DEFAULT_ORDER_ITEM_ID,
                1,
                "REQUESTED",
                "CHANGE_OF_MIND",
                "단순 변심입니다.",
                null,
                null,
                null,
                "customer@marketplace.com",
                null,
                Instant.now(),
                null,
                null,
                Instant.now(),
                Instant.now(),
                List.of());
    }

    // ===== RefundPageResult Fixtures =====

    public static RefundPageResult refundPageResult() {
        return refundPageResult(List.of(refundListResult()), 0, 20, 1L);
    }

    public static RefundPageResult refundPageResult(
            List<RefundListResult> items, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new RefundPageResult(items, pageMeta);
    }

    public static RefundPageResult emptyRefundPageResult() {
        return refundPageResult(List.of(), 0, 20, 0L);
    }

    // ===== RefundSummaryResult Fixtures =====

    public static RefundSummaryResult refundSummaryResult() {
        return new RefundSummaryResult(5L, 3L, 2L, 10L, 1L, 0L);
    }

    public static RefundSummaryResult emptyRefundSummaryResult() {
        return new RefundSummaryResult(0L, 0L, 0L, 0L, 0L, 0L);
    }
}
