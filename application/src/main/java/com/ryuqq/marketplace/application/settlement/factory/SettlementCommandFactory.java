package com.ryuqq.marketplace.application.settlement.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementPeriod;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Settlement 도메인 객체 생성 팩토리. */
@Component
public class SettlementCommandFactory {

    private final TimeProvider timeProvider;

    public SettlementCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** Entry 집계 결과로 Settlement 생성 + Entry SETTLED 전환. */
    public SettlementBundle createAggregateBundle(
            AggregateSettlementCommand command,
            SettlementAmounts amounts,
            List<SettlementEntry> entries) {
        Instant now = timeProvider.now();
        SettlementId settlementId = SettlementId.forNew(UUID.randomUUID().toString());

        SettlementPeriod period =
                SettlementPeriod.of(
                        command.periodStartDate(),
                        command.periodEndDate(),
                        SettlementCycle.valueOf(command.settlementCycle()));

        Settlement settlement =
                Settlement.forNew(
                        settlementId,
                        command.sellerId(),
                        period,
                        amounts,
                        entries.size(),
                        command.periodEndDate().plusDays(7),
                        now);

        entries.forEach(entry -> entry.markSettled(settlementId, now));

        return new SettlementBundle(settlement, entries);
    }

    public Instant now() {
        return timeProvider.now();
    }

    /** Settlement + SETTLED 전환된 Entry 목록 번들. */
    public record SettlementBundle(Settlement settlement, List<SettlementEntry> settledEntries) {}
}
