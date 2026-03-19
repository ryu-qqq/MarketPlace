package com.ryuqq.marketplace.application.settlement.entry.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateReversalEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryAmounts;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntrySourceReference;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

/** 정산 원장 도메인 객체 생성 팩토리. */
@Component
public class SettlementEntryCommandFactory {

    private static final int ELIGIBLE_DAYS_AFTER_CONFIRM = 7;

    private final TimeProvider timeProvider;

    public SettlementEntryCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 구매확정 → 판매 Entry 생성. */
    public SettlementEntry createSalesEntry(CreateSalesEntryCommand command) {
        Instant now = timeProvider.now();
        Instant eligibleAt = now.plus(ELIGIBLE_DAYS_AFTER_CONFIRM, ChronoUnit.DAYS);

        EntryAmounts amounts =
                EntryAmounts.calculate(Money.of(command.salesAmount()), command.commissionRate());
        EntrySourceReference source = EntrySourceReference.forSales(command.orderItemId());

        return SettlementEntry.forSales(
                SettlementEntryId.generate(), command.sellerId(), amounts, source, eligibleAt, now);
    }

    /** 클레임 완료 → 역분개 Entry 생성. */
    public SettlementEntry createReversalEntry(CreateReversalEntryCommand command) {
        Instant now = timeProvider.now();

        EntryType entryType = resolveEntryType(command.claimType());
        EntryAmounts amounts =
                EntryAmounts.calculate(Money.of(command.salesAmount()), command.commissionRate());
        EntrySourceReference source =
                EntrySourceReference.forClaim(
                        command.orderItemId(), command.claimId(), command.claimType());

        return SettlementEntry.forReversal(
                SettlementEntryId.generate(),
                command.sellerId(),
                entryType,
                amounts,
                source,
                null,
                now);
    }

    public Instant now() {
        return timeProvider.now();
    }

    private EntryType resolveEntryType(String claimType) {
        return switch (claimType) {
            case "CANCEL" -> EntryType.CANCEL;
            case "REFUND" -> EntryType.REFUND;
            case "EXCHANGE_OUT" -> EntryType.EXCHANGE_OUT;
            case "EXCHANGE_IN" -> EntryType.EXCHANGE_IN;
            default -> throw new IllegalArgumentException("지원하지 않는 클레임 유형: " + claimType);
        };
    }
}
