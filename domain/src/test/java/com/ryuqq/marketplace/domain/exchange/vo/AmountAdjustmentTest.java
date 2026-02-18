package com.ryuqq.marketplace.domain.exchange.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AmountAdjustment Value Object 단위 테스트")
class AmountAdjustmentTest {

    @Nested
    @DisplayName("calculate() - 정적 팩토리 메서드 테스트")
    class CalculateTest {

        @Test
        @DisplayName("교환 대상 가격이 더 높으면 추가 결제가 필요하다")
        void additionalPaymentRequiredWhenTargetPriceHigher() {
            // given
            Money originalPrice = Money.of(30000);
            Money targetPrice = Money.of(35000);
            Money collectFee = Money.of(3000);
            Money reshipFee = Money.of(3000);

            // when
            AmountAdjustment adjustment =
                    AmountAdjustment.calculate(
                            originalPrice, targetPrice, collectFee, reshipFee, FeePayer.BUYER);

            // then
            assertThat(adjustment.additionalPaymentRequired()).isTrue();
            assertThat(adjustment.partialRefundRequired()).isFalse();
            assertThat(adjustment.priceDifference()).isEqualTo(Money.of(5000));
        }

        @Test
        @DisplayName("원래 가격이 더 높으면 부분 환불이 필요하다")
        void partialRefundRequiredWhenOriginalPriceHigher() {
            // given
            Money originalPrice = Money.of(40000);
            Money targetPrice = Money.of(30000);
            Money collectFee = Money.of(3000);
            Money reshipFee = Money.of(3000);

            // when
            AmountAdjustment adjustment =
                    AmountAdjustment.calculate(
                            originalPrice, targetPrice, collectFee, reshipFee, FeePayer.SELLER);

            // then
            assertThat(adjustment.additionalPaymentRequired()).isFalse();
            assertThat(adjustment.partialRefundRequired()).isTrue();
            assertThat(adjustment.priceDifference()).isEqualTo(Money.of(10000));
        }

        @Test
        @DisplayName("가격이 같으면 추가 결제도 부분 환불도 필요하지 않다")
        void noAdjustmentWhenPricesEqual() {
            // given
            Money originalPrice = Money.of(30000);
            Money targetPrice = Money.of(30000);
            Money collectFee = Money.zero();
            Money reshipFee = Money.zero();

            // when
            AmountAdjustment adjustment =
                    AmountAdjustment.calculate(
                            originalPrice, targetPrice, collectFee, reshipFee, FeePayer.SELLER);

            // then
            assertThat(adjustment.additionalPaymentRequired()).isFalse();
            assertThat(adjustment.partialRefundRequired()).isFalse();
            assertThat(adjustment.priceDifference()).isEqualTo(Money.zero());
        }

        @Test
        @DisplayName("총 배송비는 수거비와 재배송비의 합이다")
        void totalShippingFeeIsSum() {
            // given
            Money collectFee = Money.of(3000);
            Money reshipFee = Money.of(4000);

            // when
            AmountAdjustment adjustment =
                    AmountAdjustment.calculate(
                            Money.of(30000),
                            Money.of(30000),
                            collectFee,
                            reshipFee,
                            FeePayer.BUYER);

            // then
            assertThat(adjustment.totalShippingFee()).isEqualTo(Money.of(7000));
            assertThat(adjustment.collectShippingFee()).isEqualTo(collectFee);
            assertThat(adjustment.reshipShippingFee()).isEqualTo(reshipFee);
        }

        @Test
        @DisplayName("배송비가 모두 0이면 총 배송비도 0이다")
        void totalShippingFeeIsZeroWhenBothFeesAreZero() {
            // when
            AmountAdjustment adjustment =
                    AmountAdjustment.calculate(
                            Money.of(30000),
                            Money.of(30000),
                            Money.zero(),
                            Money.zero(),
                            FeePayer.SELLER);

            // then
            assertThat(adjustment.totalShippingFee()).isEqualTo(Money.zero());
        }

        @Test
        @DisplayName("배송비 부담 주체가 설정된다")
        void feePayerIsSet() {
            // when
            AmountAdjustment buyerPays =
                    AmountAdjustment.calculate(
                            Money.of(30000),
                            Money.of(30000),
                            Money.of(3000),
                            Money.of(3000),
                            FeePayer.BUYER);
            AmountAdjustment sellerPays =
                    AmountAdjustment.calculate(
                            Money.of(30000),
                            Money.of(30000),
                            Money.of(3000),
                            Money.of(3000),
                            FeePayer.SELLER);

            // then
            assertThat(buyerPays.shippingFeePayer()).isEqualTo(FeePayer.BUYER);
            assertThat(sellerPays.shippingFeePayer()).isEqualTo(FeePayer.SELLER);
        }
    }

    @Nested
    @DisplayName("생성자 유효성 검사 테스트")
    class ValidationTest {

        @Test
        @DisplayName("originalPrice가 null이면 예외가 발생한다")
        void createWithNullOriginalPrice_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    new AmountAdjustment(
                                            null,
                                            Money.of(30000),
                                            Money.zero(),
                                            false,
                                            false,
                                            Money.zero(),
                                            Money.zero(),
                                            Money.zero(),
                                            FeePayer.BUYER))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("원래 가격은 null일 수 없습니다");
        }

        @Test
        @DisplayName("targetPrice가 null이면 예외가 발생한다")
        void createWithNullTargetPrice_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    new AmountAdjustment(
                                            Money.of(30000),
                                            null,
                                            Money.zero(),
                                            false,
                                            false,
                                            Money.zero(),
                                            Money.zero(),
                                            Money.zero(),
                                            FeePayer.BUYER))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 대상 가격은 null일 수 없습니다");
        }

        @Test
        @DisplayName("shippingFeePayer가 null이면 예외가 발생한다")
        void createWithNullShippingFeePayer_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    new AmountAdjustment(
                                            Money.of(30000),
                                            Money.of(30000),
                                            Money.zero(),
                                            false,
                                            false,
                                            Money.zero(),
                                            Money.zero(),
                                            Money.zero(),
                                            null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("배송비 부담 주체는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            AmountAdjustment adj1 = ExchangeFixtures.defaultAmountAdjustment();
            AmountAdjustment adj2 = ExchangeFixtures.defaultAmountAdjustment();

            // then
            assertThat(adj1).isEqualTo(adj2);
            assertThat(adj1.hashCode()).isEqualTo(adj2.hashCode());
        }

        @Test
        @DisplayName("가격이 다르면 동일하지 않다")
        void differentPricesAreNotEqual() {
            // given
            AmountAdjustment adj1 =
                    AmountAdjustment.calculate(
                            Money.of(30000),
                            Money.of(35000),
                            Money.of(3000),
                            Money.of(3000),
                            FeePayer.BUYER);
            AmountAdjustment adj2 =
                    AmountAdjustment.calculate(
                            Money.of(30000),
                            Money.of(40000),
                            Money.of(3000),
                            Money.of(3000),
                            FeePayer.BUYER);

            // then
            assertThat(adj1).isNotEqualTo(adj2);
        }
    }

    @Nested
    @DisplayName("Fixtures 기반 생성 테스트")
    class FixturesTest {

        @Test
        @DisplayName("defaultAmountAdjustment는 추가 결제가 필요하다")
        void defaultAmountAdjustmentRequiresAdditionalPayment() {
            // when
            AmountAdjustment adjustment = ExchangeFixtures.defaultAmountAdjustment();

            // then
            assertThat(adjustment.additionalPaymentRequired()).isTrue();
            assertThat(adjustment.originalPrice()).isEqualTo(Money.of(30000));
            assertThat(adjustment.targetPrice()).isEqualTo(Money.of(35000));
        }

        @Test
        @DisplayName("zeroAmountAdjustment는 추가 결제나 환불이 필요 없다")
        void zeroAmountAdjustmentRequiresNoAdjustment() {
            // when
            AmountAdjustment adjustment = ExchangeFixtures.zeroAmountAdjustment();

            // then
            assertThat(adjustment.additionalPaymentRequired()).isFalse();
            assertThat(adjustment.partialRefundRequired()).isFalse();
            assertThat(adjustment.totalShippingFee()).isEqualTo(Money.zero());
        }
    }
}
