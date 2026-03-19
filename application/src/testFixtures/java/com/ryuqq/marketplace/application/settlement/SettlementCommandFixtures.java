package com.ryuqq.marketplace.application.settlement;

import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;
import java.time.LocalDate;

/**
 * Settlement Application Command 테스트 Fixtures.
 *
 * <p>Settlement 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SettlementCommandFixtures {

    private SettlementCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_SETTLEMENT_CYCLE = "WEEKLY";
    public static final LocalDate DEFAULT_PERIOD_START = LocalDate.now().minusDays(7);
    public static final LocalDate DEFAULT_PERIOD_END = LocalDate.now();

    // ===== AggregateSettlementCommand =====

    public static AggregateSettlementCommand aggregateCommand() {
        return new AggregateSettlementCommand(
                DEFAULT_SELLER_ID,
                DEFAULT_PERIOD_START,
                DEFAULT_PERIOD_END,
                DEFAULT_SETTLEMENT_CYCLE);
    }

    public static AggregateSettlementCommand aggregateCommand(long sellerId) {
        return new AggregateSettlementCommand(
                sellerId, DEFAULT_PERIOD_START, DEFAULT_PERIOD_END, DEFAULT_SETTLEMENT_CYCLE);
    }

    public static AggregateSettlementCommand aggregateCommand(
            long sellerId, LocalDate startDate, LocalDate endDate) {
        return new AggregateSettlementCommand(
                sellerId, startDate, endDate, DEFAULT_SETTLEMENT_CYCLE);
    }

    public static AggregateSettlementCommand aggregateCommand(
            long sellerId, LocalDate startDate, LocalDate endDate, String settlementCycle) {
        return new AggregateSettlementCommand(sellerId, startDate, endDate, settlementCycle);
    }
}
