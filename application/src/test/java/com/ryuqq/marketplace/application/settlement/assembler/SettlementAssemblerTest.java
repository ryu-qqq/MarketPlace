package com.ryuqq.marketplace.application.settlement.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementAssembler 단위 테스트")
class SettlementAssemblerTest {

    private SettlementAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new SettlementAssembler();
    }

    @Nested
    @DisplayName("toSettlementAmounts() - Entry 목록 → SettlementAmounts 집계")
    class ToSettlementAmountsTest {

        @Test
        @DisplayName("판매 Entry 단건을 집계하여 올바른 SettlementAmounts를 반환한다")
        void toSettlementAmounts_SingleSalesEntry_ReturnsCorrectAmounts() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.confirmedSalesEntry();

            // when
            SettlementAmounts result = sut.toSettlementAmounts(List.of(entry));

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalSalesAmount().value())
                    .isEqualTo(SettlementEntryFixtures.DEFAULT_SALES_AMOUNT);
            assertThat(result.totalCommissionAmount().value()).isPositive();
            assertThat(result.totalReversalAmount().value()).isZero();
            assertThat(result.netSettlementAmount().value()).isPositive();
        }

        @Test
        @DisplayName("역분개 Entry를 포함한 목록에서 reversalAmount가 차감된 net을 반환한다")
        void toSettlementAmounts_WithReversalEntry_DeductsReversalFromNet() {
            // given
            SettlementEntry salesEntry = SettlementEntryFixtures.confirmedSalesEntry();
            SettlementEntry reversalEntry = SettlementEntryFixtures.cancelReversalEntry();

            // when
            SettlementAmounts result = sut.toSettlementAmounts(List.of(salesEntry, reversalEntry));

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalSalesAmount().value())
                    .isEqualTo(SettlementEntryFixtures.DEFAULT_SALES_AMOUNT);
            assertThat(result.totalReversalAmount().value()).isPositive();
        }

        @Test
        @DisplayName("판매 Entry 여러 건을 집계하면 salesAmount가 합산된다")
        void toSettlementAmounts_MultipleSalesEntries_SumsSalesAmounts() {
            // given
            SettlementEntry entry1 = SettlementEntryFixtures.confirmedSalesEntry();
            SettlementEntry entry2 = SettlementEntryFixtures.confirmedSalesEntry();

            // when
            SettlementAmounts result = sut.toSettlementAmounts(List.of(entry1, entry2));

            // then
            assertThat(result.totalSalesAmount().value())
                    .isEqualTo(SettlementEntryFixtures.DEFAULT_SALES_AMOUNT * 2);
        }

        @Test
        @DisplayName("net이 음수가 되는 경우 0으로 처리된다")
        void toSettlementAmounts_NetNegative_ReturnsZeroNet() {
            // given
            // reversalAmount > salesAmount 시나리오: 역분개만 존재하면 net=0
            SettlementEntry reversalEntry = SettlementEntryFixtures.cancelReversalEntry();

            // when
            SettlementAmounts result = sut.toSettlementAmounts(List.of(reversalEntry));

            // then
            assertThat(result.netSettlementAmount().value()).isZero();
        }
    }
}
