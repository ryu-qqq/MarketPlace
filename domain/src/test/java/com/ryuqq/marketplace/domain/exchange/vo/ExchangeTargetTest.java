package com.ryuqq.marketplace.domain.exchange.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeTarget Value Object 단위 테스트")
class ExchangeTargetTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 파라미터로 생성한다")
        void createWithValidParams() {
            // when
            ExchangeTarget target = new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 2);

            // then
            assertThat(target.productGroupId()).isEqualTo(1001L);
            assertThat(target.productId()).isEqualTo(2001L);
            assertThat(target.skuCode()).isEqualTo("SKU-RED-XL");
            assertThat(target.quantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("skuCode가 null이면 예외가 발생한다")
        void createWithNullSkuCode_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeTarget(1001L, 2001L, null, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("skuCode가 빈 문자열이면 예외가 발생한다")
        void createWithBlankSkuCode_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeTarget(1001L, 2001L, "", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("skuCode가 공백만 있으면 예외가 발생한다")
        void createWithWhitespaceSkuCode_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeTarget(1001L, 2001L, "   ", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("quantity가 0이면 예외가 발생한다")
        void createWithZeroQuantity_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 1 이상이어야 합니다");
        }

        @Test
        @DisplayName("quantity가 음수이면 예외가 발생한다")
        void createWithNegativeQuantity_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 1 이상이어야 합니다");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            ExchangeTarget target1 = new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 1);
            ExchangeTarget target2 = new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 1);

            // then
            assertThat(target1).isEqualTo(target2);
            assertThat(target1.hashCode()).isEqualTo(target2.hashCode());
        }

        @Test
        @DisplayName("skuCode가 다르면 동일하지 않다")
        void differentSkuCodeIsNotEqual() {
            // given
            ExchangeTarget target1 = new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 1);
            ExchangeTarget target2 = new ExchangeTarget(1001L, 2001L, "SKU-BLUE-XL", 1);

            // then
            assertThat(target1).isNotEqualTo(target2);
        }

        @Test
        @DisplayName("quantity가 다르면 동일하지 않다")
        void differentQuantityIsNotEqual() {
            // given
            ExchangeTarget target1 = new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 1);
            ExchangeTarget target2 = new ExchangeTarget(1001L, 2001L, "SKU-RED-XL", 2);

            // then
            assertThat(target1).isNotEqualTo(target2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ExchangeTarget은 record이므로 불변이다")
        void exchangeTargetIsImmutable() {
            // given
            ExchangeTarget target = ExchangeFixtures.defaultExchangeTarget();

            // then (record이므로 setter 없음, 컴파일 레벨에서 불변 보장)
            assertThat(target.skuCode()).isEqualTo("SKU-RED-XL");
            assertThat(target.quantity()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Fixtures 기반 생성 테스트")
    class FixturesTest {

        @Test
        @DisplayName("defaultExchangeTarget이 올바르게 생성된다")
        void defaultExchangeTargetCreated() {
            // when
            ExchangeTarget target = ExchangeFixtures.defaultExchangeTarget();

            // then
            assertThat(target.skuCode()).isNotBlank();
            assertThat(target.quantity()).isGreaterThan(0);
            assertThat(target.productGroupId()).isGreaterThan(0L);
            assertThat(target.productId()).isGreaterThan(0L);
        }
    }
}
