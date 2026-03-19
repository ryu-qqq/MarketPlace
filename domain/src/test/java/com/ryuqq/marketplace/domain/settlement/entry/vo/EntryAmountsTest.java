package com.ryuqq.marketplace.domain.settlement.entry.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EntryAmounts 단위 테스트")
class EntryAmountsTest {

    @Test
    @DisplayName("판매금액 50000원, 수수료율 10% → 수수료 5000원, 정산 45000원")
    void calculateCommission() {
        EntryAmounts amounts = EntryAmounts.calculate(Money.of(50000), 1000);

        assertThat(amounts.salesAmount().value()).isEqualTo(50000);
        assertThat(amounts.commissionRate()).isEqualTo(1000);
        assertThat(amounts.commissionAmount().value()).isEqualTo(5000);
        assertThat(amounts.settlementAmount().value()).isEqualTo(45000);
    }

    @Test
    @DisplayName("수수료율 0% → 수수료 0원, 정산 = 판매금액")
    void zeroCommissionRate() {
        EntryAmounts amounts = EntryAmounts.calculate(Money.of(30000), 0);

        assertThat(amounts.commissionAmount().value()).isEqualTo(0);
        assertThat(amounts.settlementAmount().value()).isEqualTo(30000);
    }

    @Test
    @DisplayName("수수료율 15% → 수수료 7500원, 정산 42500원")
    void fifteenPercentCommission() {
        EntryAmounts amounts = EntryAmounts.calculate(Money.of(50000), 1500);

        assertThat(amounts.commissionAmount().value()).isEqualTo(7500);
        assertThat(amounts.settlementAmount().value()).isEqualTo(42500);
    }

    @Test
    @DisplayName("salesAmount null → 예외")
    void nullSalesAmountThrows() {
        assertThatThrownBy(() -> EntryAmounts.calculate(null, 1000))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("음수 수수료율 → 예외")
    void negativeCommissionRateThrows() {
        assertThatThrownBy(
                        () -> new EntryAmounts(Money.of(50000), -1, Money.of(0), Money.of(50000)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
