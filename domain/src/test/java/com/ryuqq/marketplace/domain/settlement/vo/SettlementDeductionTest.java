package com.ryuqq.marketplace.domain.settlement.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementDeduction Value Object 단위 테스트")
class SettlementDeductionTest {

    @Nested
    @DisplayName("of() - 차감 항목 생성")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 차감 항목을 생성한다")
        void createWithValidValues() {
            // when
            SettlementDeduction deduction =
                    SettlementDeduction.of(
                            DeductionType.COUPON, DeductionPayer.SELLER, Money.of(5000), "쿠폰 할인");

            // then
            assertThat(deduction.type()).isEqualTo(DeductionType.COUPON);
            assertThat(deduction.payer()).isEqualTo(DeductionPayer.SELLER);
            assertThat(deduction.amount()).isEqualTo(Money.of(5000));
            assertThat(deduction.description()).isEqualTo("쿠폰 할인");
        }

        @Test
        @DisplayName("description이 null이어도 생성된다")
        void createWithNullDescription() {
            // when & then
            assertThatCode(
                            () ->
                                    SettlementDeduction.of(
                                            DeductionType.POINT,
                                            DeductionPayer.PLATFORM,
                                            Money.of(1000),
                                            null))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("type이 null이면 예외가 발생한다")
        void nullType_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    SettlementDeduction.of(
                                            null, DeductionPayer.SELLER, Money.of(1000), "설명"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("차감 유형");
        }

        @Test
        @DisplayName("payer가 null이면 예외가 발생한다")
        void nullPayer_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    SettlementDeduction.of(
                                            DeductionType.COUPON, null, Money.of(1000), "설명"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("차감 부담 주체");
        }

        @Test
        @DisplayName("amount가 null이면 예외가 발생한다")
        void nullAmount_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    SettlementDeduction.of(
                                            DeductionType.COUPON,
                                            DeductionPayer.SELLER,
                                            null,
                                            "설명"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("차감 금액");
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 SettlementDeduction은 같다")
        void sameValuesAreEqual() {
            // given
            SettlementDeduction deduction1 =
                    SettlementDeduction.of(
                            DeductionType.COUPON, DeductionPayer.SELLER, Money.of(3000), "쿠폰 할인");
            SettlementDeduction deduction2 =
                    SettlementDeduction.of(
                            DeductionType.COUPON, DeductionPayer.SELLER, Money.of(3000), "쿠폰 할인");

            // then
            assertThat(deduction1).isEqualTo(deduction2);
            assertThat(deduction1.hashCode()).isEqualTo(deduction2.hashCode());
        }

        @Test
        @DisplayName("다른 type이면 동일하지 않다")
        void differentTypeIsNotEqual() {
            // given
            SettlementDeduction deduction1 =
                    SettlementDeduction.of(
                            DeductionType.COUPON, DeductionPayer.SELLER, Money.of(1000), null);
            SettlementDeduction deduction2 =
                    SettlementDeduction.of(
                            DeductionType.MILEAGE, DeductionPayer.SELLER, Money.of(1000), null);

            // then
            assertThat(deduction1).isNotEqualTo(deduction2);
        }

        @Test
        @DisplayName("다른 payer이면 동일하지 않다")
        void differentPayerIsNotEqual() {
            // given
            SettlementDeduction deduction1 =
                    SettlementDeduction.of(
                            DeductionType.COUPON, DeductionPayer.SELLER, Money.of(1000), null);
            SettlementDeduction deduction2 =
                    SettlementDeduction.of(
                            DeductionType.COUPON, DeductionPayer.PLATFORM, Money.of(1000), null);

            // then
            assertThat(deduction1).isNotEqualTo(deduction2);
        }
    }
}
