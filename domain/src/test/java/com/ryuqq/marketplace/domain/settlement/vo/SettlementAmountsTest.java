package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import java.util.List;
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
            // when
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();

            // then
            assertThat(amounts).isNotNull();
            assertThat(amounts.salesAmount()).isEqualTo(Money.of(100000));
            assertThat(amounts.feeAmount()).isEqualTo(Money.of(10000));
            assertThat(amounts.feeRate()).isEqualTo(10);
        }

        @Test
        @DisplayName("salesAmount가 null이면 예외가 발생한다")
        void nullSalesAmount_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            null,
                                            List.of(),
                                            Money.of(1000),
                                            10,
                                            Money.of(9000),
                                            Money.of(9000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("판매 금액");
        }

        @Test
        @DisplayName("feeAmount가 null이면 예외가 발생한다")
        void nullFeeAmount_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(10000),
                                            List.of(),
                                            null,
                                            10,
                                            Money.of(9000),
                                            Money.of(9000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수수료 금액");
        }

        @Test
        @DisplayName("feeRate가 음수이면 예외가 발생한다")
        void negativeFeeRate_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(10000),
                                            List.of(),
                                            Money.of(1000),
                                            -1,
                                            Money.of(9000),
                                            Money.of(9000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수수료율");
        }

        @Test
        @DisplayName("expectedSettlementAmount가 null이면 예외가 발생한다")
        void nullExpectedSettlementAmount_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(10000),
                                            List.of(),
                                            Money.of(1000),
                                            10,
                                            null,
                                            Money.of(9000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("예상 정산 금액");
        }

        @Test
        @DisplayName("settlementAmount가 null이면 예외가 발생한다")
        void nullSettlementAmount_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    new SettlementAmounts(
                                            Money.of(10000),
                                            List.of(),
                                            Money.of(1000),
                                            10,
                                            Money.of(9000),
                                            null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("정산 금액");
        }

        @Test
        @DisplayName("deductions가 null이면 빈 리스트로 처리된다")
        void nullDeductionsBecomesEmptyList() {
            // when
            SettlementAmounts amounts =
                    new SettlementAmounts(
                            Money.of(10000),
                            null,
                            Money.of(1000),
                            10,
                            Money.of(9000),
                            Money.of(9000));

            // then
            assertThat(amounts.deductions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("totalDeductionAmount() - 전체 차감 금액 합계")
    class TotalDeductionAmountTest {

        @Test
        @DisplayName("셀러 부담 + 플랫폼 부담 차감 금액 합계를 반환한다")
        void totalDeductionAmountIncludesBoth() {
            // given
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();

            // when
            Money total = amounts.totalDeductionAmount();

            // then
            assertThat(total).isEqualTo(Money.of(4000));
        }

        @Test
        @DisplayName("차감 항목이 없으면 0을 반환한다")
        void totalDeductionAmountIsZeroWhenNoDeductions() {
            // given
            SettlementAmounts amounts = SettlementFixtures.settlementAmountsWithoutDeductions();

            // when
            Money total = amounts.totalDeductionAmount();

            // then
            assertThat(total).isEqualTo(Money.zero());
        }
    }

    @Nested
    @DisplayName("sellerDeductionAmount() - 셀러 부담 차감 금액 합계")
    class SellerDeductionAmountTest {

        @Test
        @DisplayName("셀러 부담 차감 항목만 합산한다")
        void sellerDeductionAmountOnlySellerPayer() {
            // given
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();

            // when
            Money sellerAmount = amounts.sellerDeductionAmount();

            // then
            assertThat(sellerAmount).isEqualTo(Money.of(3000));
        }

        @Test
        @DisplayName("차감 항목이 없으면 0을 반환한다")
        void sellerDeductionAmountIsZeroWhenNoDeductions() {
            // given
            SettlementAmounts amounts = SettlementFixtures.settlementAmountsWithoutDeductions();

            // when
            Money sellerAmount = amounts.sellerDeductionAmount();

            // then
            assertThat(sellerAmount).isEqualTo(Money.zero());
        }
    }

    @Nested
    @DisplayName("platformDeductionAmount() - 플랫폼 부담 차감 금액 합계")
    class PlatformDeductionAmountTest {

        @Test
        @DisplayName("플랫폼 부담 차감 항목만 합산한다")
        void platformDeductionAmountOnlyPlatformPayer() {
            // given
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();

            // when
            Money platformAmount = amounts.platformDeductionAmount();

            // then
            assertThat(platformAmount).isEqualTo(Money.of(1000));
        }

        @Test
        @DisplayName("차감 항목이 없으면 0을 반환한다")
        void platformDeductionAmountIsZeroWhenNoDeductions() {
            // given
            SettlementAmounts amounts = SettlementFixtures.settlementAmountsWithoutDeductions();

            // when
            Money platformAmount = amounts.platformDeductionAmount();

            // then
            assertThat(platformAmount).isEqualTo(Money.zero());
        }
    }

    @Nested
    @DisplayName("deductions 불변성 검증")
    class DeductionsImmutabilityTest {

        @Test
        @DisplayName("생성 후 deductions 리스트를 수정할 수 없다")
        void deductionsListIsUnmodifiable() {
            // given
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();

            // when & then
            assertThatThrownBy(
                            () ->
                                    amounts.deductions()
                                            .add(SettlementFixtures.defaultSellerDeduction()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 SettlementAmounts는 같다")
        void sameValuesAreEqual() {
            // given
            SettlementAmounts amounts1 = SettlementFixtures.settlementAmountsWithoutDeductions();
            SettlementAmounts amounts2 = SettlementFixtures.settlementAmountsWithoutDeductions();

            // then
            assertThat(amounts1).isEqualTo(amounts2);
            assertThat(amounts1.hashCode()).isEqualTo(amounts2.hashCode());
        }
    }
}
