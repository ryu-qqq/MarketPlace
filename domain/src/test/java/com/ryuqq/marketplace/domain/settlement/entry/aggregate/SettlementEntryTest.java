package com.ryuqq.marketplace.domain.settlement.entry.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.entry.exception.SettlementEntryException;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryAmounts;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntrySourceReference;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementEntry 단위 테스트")
class SettlementEntryTest {

    private static final long SELLER_ID = 100L;
    private static final Instant NOW = Instant.now();

    @Nested
    @DisplayName("forSales - 판매 Entry 생성")
    class ForSalesTest {

        @Test
        @DisplayName("판매 Entry 생성 시 PENDING 상태로 시작한다")
        void createSalesEntry() {
            EntryAmounts amounts = EntryAmounts.calculate(Money.of(50000), 1000);
            EntrySourceReference source = EntrySourceReference.forSales(1001L);

            SettlementEntry entry =
                    SettlementEntry.forSales(
                            SettlementEntryId.generate(),
                            SELLER_ID,
                            amounts,
                            source,
                            NOW.plus(7, ChronoUnit.DAYS),
                            NOW);

            assertThat(entry.status()).isEqualTo(EntryStatus.PENDING);
            assertThat(entry.entryType()).isEqualTo(EntryType.SALES);
            assertThat(entry.sellerId()).isEqualTo(SELLER_ID);
            assertThat(entry.source().orderItemId()).isEqualTo(1001L);
            assertThat(entry.reversalOfEntryId()).isNull();
            assertThat(entry.settlementId()).isNull();
            assertThat(entry.pollEvents()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("forReversal - 역분개 Entry 생성")
    class ForReversalTest {

        @Test
        @DisplayName("취소 역분개 Entry를 생성한다")
        void createCancelReversalEntry() {
            EntryAmounts amounts = EntryAmounts.calculate(Money.of(30000), 1000);
            EntrySourceReference source =
                    EntrySourceReference.forClaim(1001L, "cancel-001", "CANCEL");
            SettlementEntryId originalId = SettlementEntryId.generate();

            SettlementEntry entry =
                    SettlementEntry.forReversal(
                            SettlementEntryId.generate(),
                            SELLER_ID,
                            EntryType.CANCEL,
                            amounts,
                            source,
                            originalId,
                            NOW);

            assertThat(entry.status()).isEqualTo(EntryStatus.PENDING);
            assertThat(entry.entryType()).isEqualTo(EntryType.CANCEL);
            assertThat(entry.source().claimId()).isEqualTo("cancel-001");
            assertThat(entry.source().claimType()).isEqualTo("CANCEL");
            assertThat(entry.reversalOfEntryId()).isEqualTo(originalId);
            assertThat(entry.eligibleAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("confirm - PENDING → CONFIRMED")
    class ConfirmTest {

        @Test
        @DisplayName("PENDING 상태에서 CONFIRMED로 전이한다")
        void confirmFromPending() {
            SettlementEntry entry = createSalesEntry();
            entry.pollEvents(); // 생성 이벤트 소비

            entry.confirm(NOW);

            assertThat(entry.status()).isEqualTo(EntryStatus.CONFIRMED);
            assertThat(entry.pollEvents()).hasSize(2); // Confirmed + StatusChanged
        }

        @Test
        @DisplayName("CONFIRMED 상태에서 confirm 호출 시 예외")
        void confirmFromConfirmedThrows() {
            SettlementEntry entry = createSalesEntry();
            entry.confirm(NOW);

            assertThatThrownBy(() -> entry.confirm(NOW))
                    .isInstanceOf(SettlementEntryException.class);
        }
    }

    @Nested
    @DisplayName("markSettled - CONFIRMED → SETTLED")
    class MarkSettledTest {

        @Test
        @DisplayName("CONFIRMED 상태에서 SETTLED로 전이한다")
        void markSettledFromConfirmed() {
            SettlementEntry entry = createSalesEntry();
            entry.confirm(NOW);
            entry.pollEvents();

            SettlementId settlementId = SettlementId.of("stl-001");
            entry.markSettled(settlementId, NOW);

            assertThat(entry.status()).isEqualTo(EntryStatus.SETTLED);
            assertThat(entry.settlementId()).isEqualTo(settlementId);
            assertThat(entry.pollEvents()).hasSize(1); // StatusChanged
        }

        @Test
        @DisplayName("PENDING 상태에서 markSettled 호출 시 예외")
        void markSettledFromPendingThrows() {
            SettlementEntry entry = createSalesEntry();

            assertThatThrownBy(() -> entry.markSettled(SettlementId.of("stl-001"), NOW))
                    .isInstanceOf(SettlementEntryException.class);
        }
    }

    private SettlementEntry createSalesEntry() {
        EntryAmounts amounts = EntryAmounts.calculate(Money.of(50000), 1000);
        EntrySourceReference source = EntrySourceReference.forSales(1001L);
        return SettlementEntry.forSales(
                SettlementEntryId.generate(),
                SELLER_ID,
                amounts,
                source,
                NOW.plus(7, ChronoUnit.DAYS),
                NOW);
    }
}
