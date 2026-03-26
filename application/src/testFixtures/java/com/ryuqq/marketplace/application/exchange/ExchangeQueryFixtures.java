package com.ryuqq.marketplace.application.exchange;

import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeListResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.time.Instant;
import java.util.List;

/**
 * Exchange Query 테스트 Fixtures.
 *
 * <p>Exchange 관련 Query 파라미터 및 응답 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExchangeQueryFixtures {

    private ExchangeQueryFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_CLAIM_ID = "01900000-0000-7000-0000-000000000001";
    private static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";

    // ===== ExchangeSearchParams Fixtures =====

    public static ExchangeSearchParams searchParams() {
        return new ExchangeSearchParams(
                null, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static ExchangeSearchParams searchParams(int page, int size) {
        return new ExchangeSearchParams(
                null, null, null, null, null, null, "CREATED_AT", "DESC", page, size);
    }

    public static ExchangeSearchParams searchParams(List<String> statuses) {
        return new ExchangeSearchParams(
                statuses, null, null, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static ExchangeSearchParams searchParams(
            List<String> statuses, String searchField, String searchWord) {
        return new ExchangeSearchParams(
                statuses, searchField, searchWord, null, null, null, "CREATED_AT", "DESC", 0, 20);
    }

    public static ExchangeSearchParams searchParamsWithDateRange(String startDate, String endDate) {
        return new ExchangeSearchParams(
                null, null, null, "REQUESTED", startDate, endDate, "CREATED_AT", "DESC", 0, 20);
    }

    // ===== ExchangeListResult Fixtures =====

    public static ExchangeListResult exchangeListResult(String claimId) {
        return new ExchangeListResult(
                claimId,
                "EXC-20260218-0001",
                DEFAULT_ORDER_ITEM_ID,
                1,
                "REQUESTED",
                "SIZE_CHANGE",
                "사이즈가 맞지 않아 교환 요청합니다",
                "SKU-RED-XL",
                1,
                null,
                "buyer@example.com",
                null,
                Instant.now(),
                null,
                null);
    }

    public static ExchangeListResult exchangeListResult(String claimId, String status) {
        return new ExchangeListResult(
                claimId,
                "EXC-20260218-0001",
                DEFAULT_ORDER_ITEM_ID,
                1,
                status,
                "SIZE_CHANGE",
                "사이즈가 맞지 않아 교환 요청합니다",
                "SKU-RED-XL",
                1,
                null,
                "buyer@example.com",
                null,
                Instant.now(),
                null,
                null);
    }

    // ===== ExchangePageResult Fixtures =====

    public static ExchangePageResult exchangePageResult() {
        List<ExchangeListResult> items = List.of(exchangeListResult(DEFAULT_CLAIM_ID));
        PageMeta pageMeta = PageMeta.of(0, 20, 1L);
        return new ExchangePageResult(items, pageMeta);
    }

    public static ExchangePageResult emptyExchangePageResult() {
        PageMeta pageMeta = PageMeta.of(0, 20, 0L);
        return new ExchangePageResult(List.of(), pageMeta);
    }

    // ===== ExchangeSummaryResult Fixtures =====

    public static ExchangeSummaryResult exchangeSummaryResult() {
        return new ExchangeSummaryResult(5L, 3L, 2L, 1L, 4L, 10L, 1L, 0L);
    }

    public static ExchangeSummaryResult emptySummaryResult() {
        return new ExchangeSummaryResult(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    }

    // ===== ExchangeDetailResult Fixtures =====

    public static ExchangeDetailResult exchangeDetailResult(String claimId) {
        return new ExchangeDetailResult(
                claimId,
                "EXC-20260218-0001",
                DEFAULT_ORDER_ITEM_ID,
                100L,
                1,
                "REQUESTED",
                "SIZE_CHANGE",
                "사이즈가 맞지 않아 교환 요청합니다",
                new ExchangeDetailResult.ExchangeOptionResult(
                        1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 1),
                null,
                null,
                null,
                "buyer@example.com",
                null,
                Instant.now(),
                null,
                null,
                Instant.now(),
                Instant.now(),
                List.of());
    }
}
