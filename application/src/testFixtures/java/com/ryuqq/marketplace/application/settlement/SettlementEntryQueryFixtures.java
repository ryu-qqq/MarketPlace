package com.ryuqq.marketplace.application.settlement;

import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.dto.response.SettlementEntryListResult;
import java.time.Instant;
import java.util.List;

/**
 * SettlementEntry Application Query 테스트 Fixtures.
 *
 * <p>SettlementEntry 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SettlementEntryQueryFixtures {

    private SettlementEntryQueryFixtures() {}

    // ===== SettlementEntrySearchParams Fixtures =====

    public static SettlementEntrySearchParams searchParams() {
        return new SettlementEntrySearchParams(
                List.of("PENDING"), List.of(100L), null, null, "2024-01-01", "2024-12-31", 0, 20);
    }

    public static SettlementEntrySearchParams searchParams(int page, int size) {
        return new SettlementEntrySearchParams(
                List.of("PENDING"),
                List.of(100L),
                null,
                null,
                "2024-01-01",
                "2024-12-31",
                page,
                size);
    }

    public static SettlementEntrySearchParams searchParams(List<String> statuses) {
        return new SettlementEntrySearchParams(
                statuses, List.of(100L), null, null, "2024-01-01", "2024-12-31", 0, 20);
    }

    public static SettlementEntrySearchParams emptySearchParams() {
        return new SettlementEntrySearchParams(List.of(), List.of(), null, null, null, null, 0, 20);
    }

    // ===== SettlementEntryListResult Fixtures =====

    public static SettlementEntryListResult settlementEntryListResult(String entryId) {
        return new SettlementEntryListResult(
                entryId,
                "PENDING",
                100L,
                "SALES",
                1001L,
                50000,
                1000,
                5000,
                45000,
                null,
                null,
                Instant.now().plusSeconds(604800),
                null,
                null,
                Instant.now());
    }

    public static SettlementEntryListResult confirmedEntryListResult(String entryId) {
        return new SettlementEntryListResult(
                entryId,
                "CONFIRMED",
                100L,
                "SALES",
                1001L,
                50000,
                1000,
                5000,
                45000,
                null,
                null,
                Instant.now().plusSeconds(604800),
                null,
                null,
                Instant.now());
    }

    public static SettlementEntryListResult cancelReversalEntryListResult(String entryId) {
        return new SettlementEntryListResult(
                entryId,
                "PENDING",
                100L,
                "CANCEL",
                1001L,
                50000,
                1000,
                5000,
                45000,
                "claim-test-001",
                "CANCEL",
                null,
                null,
                null,
                Instant.now());
    }
}
