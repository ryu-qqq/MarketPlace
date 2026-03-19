package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Money Value Object 단위 테스트")
class MoneyTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("양수 금액으로 Money를 생성한다")
        void createWithPositiveValue() {
            Money money = Money.of(1000);

            assertThat(money.value()).isEqualTo(1000);
        }

        @Test
        @DisplayName("0원으로 Money를 생성한다")
        void createWithZeroValue() {
            Money money = Money.of(0);

            assertThat(money.value()).isEqualTo(0);
        }

        @Test
        @DisplayName("zero() 팩토리는 0원을 반환한다")
        void zeroFactoryReturnsZeroMoney() {
            Money money = Money.zero();

            assertThat(money.value()).isEqualTo(0);
            assertThat(money.isZero()).isTrue();
        }

        @Test
        @DisplayName("음수 금액으로 생성하면 예외가 발생한다")
        void createWithNegativeValueThrowsException() {
            assertThatThrownBy(() -> Money.of(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0 이상");
        }
    }

    @Nested
    @DisplayName("산술 연산 테스트")
    class ArithmeticTest {

        @Test
        @DisplayName("두 금액을 더한다")
        void addTwoMoneys() {
            Money a = Money.of(1000);
            Money b = Money.of(500);

            Money result = a.add(b);

            assertThat(result.value()).isEqualTo(1500);
        }

        @Test
        @DisplayName("큰 금액에서 작은 금액을 뺀다")
        void subtractSmallerFromLarger() {
            Money a = Money.of(1000);
            Money b = Money.of(300);

            Money result = a.subtract(b);

            assertThat(result.value()).isEqualTo(700);
        }

        @Test
        @DisplayName("뺄셈 결과가 음수이면 예외가 발생한다")
        void subtractResultNegativeThrowsException() {
            Money a = Money.of(300);
            Money b = Money.of(1000);

            assertThatThrownBy(() -> a.subtract(b))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("음수");
        }

        @Test
        @DisplayName("금액에 양수를 곱한다")
        void multiplyByPositiveMultiplier() {
            Money money = Money.of(1000);

            Money result = money.multiply(3);

            assertThat(result.value()).isEqualTo(3000);
        }

        @Test
        @DisplayName("금액에 0을 곱하면 0원이 된다")
        void multiplyByZeroReturnsZero() {
            Money money = Money.of(1000);

            Money result = money.multiply(0);

            assertThat(result.isZero()).isTrue();
        }

        @Test
        @DisplayName("음수로 곱하면 예외가 발생한다")
        void multiplyByNegativeThrowsException() {
            Money money = Money.of(1000);

            assertThatThrownBy(() -> money.multiply(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0 이상");
        }
    }

    @Nested
    @DisplayName("비교 테스트")
    class ComparisonTest {

        @Test
        @DisplayName("더 큰 금액이면 isGreaterThan이 true이다")
        void greaterThanReturnsTrueWhenLarger() {
            Money larger = Money.of(1000);
            Money smaller = Money.of(500);

            assertThat(larger.isGreaterThan(smaller)).isTrue();
            assertThat(smaller.isGreaterThan(larger)).isFalse();
        }

        @Test
        @DisplayName("같은 금액이면 isGreaterThanOrEqual이 true이다")
        void greaterThanOrEqualReturnsTrueWhenEqual() {
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            assertThat(a.isGreaterThanOrEqual(b)).isTrue();
        }

        @Test
        @DisplayName("더 작은 금액이면 isLessThan이 true이다")
        void lessThanReturnsTrueWhenSmaller() {
            Money smaller = Money.of(500);
            Money larger = Money.of(1000);

            assertThat(smaller.isLessThan(larger)).isTrue();
            assertThat(larger.isLessThan(smaller)).isFalse();
        }

        @Test
        @DisplayName("같은 금액이면 isLessThanOrEqual이 true이다")
        void lessThanOrEqualReturnsTrueWhenEqual() {
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            assertThat(a.isLessThanOrEqual(b)).isTrue();
        }
    }

    @Nested
    @DisplayName("할인율 계산 테스트")
    class DiscountRateTest {

        @Test
        @DisplayName("정가 10000원, 판매가 8000원이면 할인율은 20%이다")
        void calculateDiscountRate() {
            Money regular = Money.of(10000);
            Money current = Money.of(8000);

            int rate = Money.discountRate(regular, current);

            assertThat(rate).isEqualTo(20);
        }

        @Test
        @DisplayName("정가가 0원이면 할인율은 0%이다")
        void discountRateIsZeroWhenRegularIsZero() {
            Money regular = Money.zero();
            Money current = Money.of(1000);

            int rate = Money.discountRate(regular, current);

            assertThat(rate).isEqualTo(0);
        }

        @Test
        @DisplayName("판매가가 정가 이상이면 할인율은 0%이다")
        void discountRateIsZeroWhenCurrentIsNotLessThanRegular() {
            Money regular = Money.of(1000);
            Money current = Money.of(1000);

            int rate = Money.discountRate(regular, current);

            assertThat(rate).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 금액은 동일하다")
        void sameValueIsEqual() {
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("다른 금액은 동일하지 않다")
        void differentValueIsNotEqual() {
            Money a = Money.of(1000);
            Money b = Money.of(2000);

            assertThat(a).isNotEqualTo(b);
        }
    }
}
