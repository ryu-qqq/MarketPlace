package com.ryuqq.marketplace.domain.settlement.entry;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryAmounts;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntrySourceReference;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** 정산 원장 테스트 Fixtures. */
public final class SettlementEntryFixtures {

    private SettlementEntryFixtures() {}

    public static final long DEFAULT_SELLER_ID = 100L;
    public static final long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final int DEFAULT_SALES_AMOUNT = 50000;
    public static final int DEFAULT_COMMISSION_RATE = 1000; // 10%

    public static SettlementEntry salesEntry() {
        return salesEntry(DEFAULT_SELLER_ID, DEFAULT_ORDER_ITEM_ID);
    }

    public static SettlementEntry salesEntry(long sellerId, long orderItemId) {
        Instant now = Instant.now();
        EntryAmounts amounts =
                EntryAmounts.calculate(Money.of(DEFAULT_SALES_AMOUNT), DEFAULT_COMMISSION_RATE);
        EntrySourceReference source = EntrySourceReference.forSales(orderItemId);

        return SettlementEntry.forSales(
                SettlementEntryId.generate(),
                sellerId,
                amounts,
                source,
                now.plus(7, ChronoUnit.DAYS),
                now);
    }

    public static SettlementEntry cancelReversalEntry() {
        return cancelReversalEntry(DEFAULT_SELLER_ID, DEFAULT_ORDER_ITEM_ID, "cancel-001");
    }

    public static SettlementEntry cancelReversalEntry(
            long sellerId, long orderItemId, String cancelId) {
        Instant now = Instant.now();
        EntryAmounts amounts =
                EntryAmounts.calculate(Money.of(DEFAULT_SALES_AMOUNT), DEFAULT_COMMISSION_RATE);
        EntrySourceReference source =
                EntrySourceReference.forClaim(orderItemId, cancelId, "CANCEL");

        return SettlementEntry.forReversal(
                SettlementEntryId.generate(),
                sellerId,
                EntryType.CANCEL,
                amounts,
                source,
                null,
                now);
    }

    public static SettlementEntry confirmedSalesEntry() {
        SettlementEntry entry = salesEntry();
        entry.confirm(Instant.now());
        return entry;
    }

    public static EntryAmounts defaultAmounts() {
        return EntryAmounts.calculate(Money.of(DEFAULT_SALES_AMOUNT), DEFAULT_COMMISSION_RATE);
    }
}
