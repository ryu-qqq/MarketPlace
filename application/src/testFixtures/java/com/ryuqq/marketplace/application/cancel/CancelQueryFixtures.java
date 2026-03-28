package com.ryuqq.marketplace.application.cancel;

import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * Cancel Application Query 테스트 Fixtures.
 *
 * <p>Cancel 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CancelQueryFixtures {

    private CancelQueryFixtures() {}

    private static final String DEFAULT_CANCEL_ID = "01900000-0000-7000-8000-000000000001";
    private static final Long DEFAULT_ORDER_ITEM_ID = 1001L;

    // ===== CancelSearchParams Fixtures =====

    public static CancelSearchParams searchParams() {
        return new CancelSearchParams(
                null, null, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static CancelSearchParams searchParams(int page, int size) {
        return new CancelSearchParams(
                null, null, null, null, null, null, null, "CREATED_AT", "DESC", page, size);
    }

    public static CancelSearchParams searchParamsByStatus(String status) {
        return new CancelSearchParams(
                List.of(status), null, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static CancelSearchParams searchParamsByDateRange(String startDate, String endDate) {
        return new CancelSearchParams(
                null,
                null,
                null,
                null,
                "REQUESTED",
                startDate,
                endDate,
                "CREATED_AT",
                "DESC",
                0,
                20);
    }

    // ===== CancelListResult Fixtures =====

    public static CancelListResult cancelListResult() {
        return cancelListResult(DEFAULT_CANCEL_ID);
    }

    public static CancelListResult cancelListResult(String cancelId) {
        return new CancelListResult(
                cancelId,
                "CAN-20240101-0001",
                DEFAULT_ORDER_ITEM_ID,
                2,
                "BUYER_CANCEL",
                "REQUESTED",
                "CHANGE_OF_MIND",
                null,
                null,
                null,
                "buyer@marketplace.com",
                null,
                Instant.now(),
                null,
                null);
    }

    // ===== CancelDetailResult Fixtures =====

    public static CancelDetailResult cancelDetailResult() {
        return cancelDetailResult(DEFAULT_CANCEL_ID);
    }

    public static CancelDetailResult cancelDetailResult(String cancelId) {
        return new CancelDetailResult(
                cancelId,
                "CAN-20240101-0001",
                DEFAULT_ORDER_ITEM_ID,
                2,
                "BUYER_CANCEL",
                "REQUESTED",
                "CHANGE_OF_MIND",
                null,
                null,
                "buyer@marketplace.com",
                null,
                Instant.now(),
                null,
                null,
                Instant.now(),
                Instant.now(),
                List.of());
    }

    // ===== CancelPageResult Fixtures =====

    public static CancelPageResult cancelPageResult() {
        return cancelPageResult(List.of(cancelListResult()), 0, 20, 1L);
    }

    public static CancelPageResult cancelPageResult(
            List<CancelListResult> items, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new CancelPageResult(items, pageMeta);
    }

    public static CancelPageResult emptyCancelPageResult() {
        return cancelPageResult(List.of(), 0, 20, 0L);
    }

    // ===== CancelSummaryResult Fixtures =====

    public static CancelSummaryResult cancelSummaryResult() {
        return new CancelSummaryResult(5L, 3L, 1L, 10L, 2L);
    }

    public static CancelSummaryResult emptyCancelSummaryResult() {
        return new CancelSummaryResult(0L, 0L, 0L, 0L, 0L);
    }
}
