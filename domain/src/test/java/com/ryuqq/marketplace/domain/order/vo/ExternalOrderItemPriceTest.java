package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalOrderItemPrice Value Object 테스트")
class ExternalOrderItemPriceTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 ExternalOrderItemPrice를 생성한다")
        void createWithValidValues() {
            // when
            ExternalOrderItemPrice price = OrderFixtures.defaultExternalOrderItemPrice();

            // then
            assertThat(price.unitPrice()).isEqualTo(Money.of(10000));
            assertThat(price.quantity()).isEqualTo(2);
            assertThat(price.totalAmount()).isEqualTo(Money.of(20000));
            assertThat(price.paymentAmount()).isEqualTo(Money.of(20000));
        }

        @Test
        @DisplayName("개당 판매가가 null이면 예외가 발생한다")
        void createWithNullUnitPrice_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderItemPrice.of(
                                            null,
                                            1,
                                            Money.of(10000),
                                            Money.zero(),
                                            Money.of(10000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("개당 판매가는 필수");
        }

        @Test
        @DisplayName("수량이 0이면 예외가 발생한다")
        void createWithZeroQuantity_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderItemPrice.of(
                                            Money.of(10000),
                                            0,
                                            Money.of(10000),
                                            Money.zero(),
                                            Money.of(10000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 1 이상");
        }

        @Test
        @DisplayName("수량이 음수이면 예외가 발생한다")
        void createWithNegativeQuantity_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderItemPrice.of(
                                            Money.of(10000),
                                            -1,
                                            Money.of(10000),
                                            Money.zero(),
                                            Money.of(10000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 1 이상");
        }

        @Test
        @DisplayName("합계 금액이 null이면 예외가 발생한다")
        void createWithNullTotalAmount_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderItemPrice.of(
                                            Money.of(10000),
                                            1,
                                            null,
                                            Money.zero(),
                                            Money.of(10000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("합계 금액은 필수");
        }

        @Test
        @DisplayName("실결제 금액이 null이면 예외가 발생한다")
        void createWithNullPaymentAmount_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderItemPrice.of(
                                            Money.of(10000),
                                            1,
                                            Money.of(10000),
                                            Money.zero(),
                                            null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("실결제 금액은 필수");
        }

        @Test
        @DisplayName("할인금액이 null이면 Money.zero()로 대체된다")
        void createWithNullDiscountAmount_DefaultsToZero() {
            // when
            ExternalOrderItemPrice price =
                    ExternalOrderItemPrice.of(
                            Money.of(10000), 1, Money.of(10000), null, Money.of(10000));

            // then
            assertThat(price.discountAmount()).isEqualTo(Money.zero());
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 ExternalOrderItemPrice는 동일하다")
        void sameValuesAreEqual() {
            // when
            ExternalOrderItemPrice price1 = OrderFixtures.defaultExternalOrderItemPrice();
            ExternalOrderItemPrice price2 = OrderFixtures.defaultExternalOrderItemPrice();

            // then
            assertThat(price1).isEqualTo(price2);
        }
    }
}
