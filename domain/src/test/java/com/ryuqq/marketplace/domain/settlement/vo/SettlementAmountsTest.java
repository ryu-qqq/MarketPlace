package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementAmounts Value Object 단위 테스트")
class SettlementAmountsTest {

    @Nested
    @DisplayName("생성 유효성 검증")
    class CreationValidationTest {

        @Test
        @DisplayName("유효한 값으로 SettlementAmounts를 생성한다")
        void createWithValidValues() {
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();

            assertThat(amounts).isNotNull();
            assertThat(amounts.totalSalesAmount()).isEqualTo(Money.of(100000));
            assertThat(amounts.totalCommissionAmount()).isEqualTo(Money.of(10000));
            assertThat(amounts.totalReversalAmount()).isEqualTo(Money.of(5000));
            assertThat(amounts.netSettlementAmount()).isEqualTo(Money.of(85000));
        }

        @Test
        @DisplayName("totalSalesAmount가 null이면 예외")
        void nullTotalSalesAmountThrows() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            null, Money.of(0), Money.of(0), Money.of(0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("총 판매 금액");
        }

        @Test
        @DisplayName("totalCommissionAmount가 null이면 예외")
        void nullTotalCommissionAmountThrows() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(0), null, Money.of(0), Money.of(0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("총 수수료 금액");
        }

        @Test
        @DisplayName("totalReversalAmount가 null이면 예외")
        void nullTotalReversalAmountThrows() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(0), Money.of(0), null, Money.of(0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("총 역분개 금액");
        }

        @Test
        @DisplayName("netSettlementAmount가 null이면 예외")
        void nullNetSettlementAmountThrows() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(0), Money.of(0), Money.of(0), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("순 정산 금액");
        }
    }

    @Nested
    @DisplayName("팩토리 메서드 검증")
    class FactoryMethodTest {

        @Test
        @DisplayName("zero()로 생성하면 모든 금액이 0")
        void zeroCreatesAllZero() {
            SettlementAmounts amounts = SettlementAmounts.zero();

            assertThat(amounts.totalSalesAmount()).isEqualTo(Money.zero());
            assertThat(amounts.totalCommissionAmount()).isEqualTo(Money.zero());
            assertThat(amounts.totalReversalAmount()).isEqualTo(Money.zero());
            assertThat(amounts.netSettlementAmount()).isEqualTo(Money.zero());
        }

        @Test
        @DisplayName("of()로 생성")
        void ofCreatesWithValues() {
            SettlementAmounts amounts =
                    SettlementAmounts.of(
                            Money.of(50000), Money.of(5000), Money.of(3000), Money.of(42000));

            assertThat(amounts.totalSalesAmount().value()).isEqualTo(50000);
            assertThat(amounts.netSettlementAmount().value()).isEqualTo(42000);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 SettlementAmounts는 같다")
        void sameValuesAreEqual() {
            SettlementAmounts amounts1 = SettlementAmounts.zero();
            SettlementAmounts amounts2 = SettlementAmounts.zero();

            assertThat(amounts1).isEqualTo(amounts2);
            assertThat(amounts1.hashCode()).isEqualTo(amounts2.hashCode());
        }
    }
}
