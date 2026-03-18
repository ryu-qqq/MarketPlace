package com.ryuqq.marketplace.domain.exchange.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeOption Value Object 단위 테스트")
class ExchangeOptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 파라미터로 생성한다")
        void createWithValidParams() {
            // when
            ExchangeOption option = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 2);

            // then
            assertThat(option.originalProductId()).isEqualTo(1000L);
            assertThat(option.originalSkuCode()).isEqualTo("SKU-RED-M");
            assertThat(option.targetProductGroupId()).isEqualTo(1001L);
            assertThat(option.targetProductId()).isEqualTo(2001L);
            assertThat(option.targetSkuCode()).isEqualTo("SKU-RED-XL");
            assertThat(option.quantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("originalSkuCode가 null이면 예외가 발생한다")
        void createWithNullOriginalSkuCode_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeOption(1000L, null, 1001L, 2001L, "SKU-RED-XL", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("원래 SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("targetSkuCode가 null이면 예외가 발생한다")
        void createWithNullTargetSkuCode_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, null, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 대상 SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("targetSkuCode가 빈 문자열이면 예외가 발생한다")
        void createWithBlankTargetSkuCode_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("교환 대상 SKU 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("quantity가 0이면 예외가 발생한다")
        void createWithZeroQuantity_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 1 이상이어야 합니다");
        }

        @Test
        @DisplayName("quantity가 음수이면 예외가 발생한다")
        void createWithNegativeQuantity_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", -1))
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
            ExchangeOption option1 = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 1);
            ExchangeOption option2 = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 1);

            // then
            assertThat(option1).isEqualTo(option2);
            assertThat(option1.hashCode()).isEqualTo(option2.hashCode());
        }

        @Test
        @DisplayName("targetSkuCode가 다르면 동일하지 않다")
        void differentTargetSkuCodeIsNotEqual() {
            // given
            ExchangeOption option1 = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 1);
            ExchangeOption option2 = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-BLUE-XL", 1);

            // then
            assertThat(option1).isNotEqualTo(option2);
        }

        @Test
        @DisplayName("quantity가 다르면 동일하지 않다")
        void differentQuantityIsNotEqual() {
            // given
            ExchangeOption option1 = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 1);
            ExchangeOption option2 = new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 2);

            // then
            assertThat(option1).isNotEqualTo(option2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ExchangeOption은 record이므로 불변이다")
        void exchangeOptionIsImmutable() {
            // given
            ExchangeOption option = ExchangeFixtures.defaultExchangeOption();

            // then (record이므로 setter 없음, 컴파일 레벨에서 불변 보장)
            assertThat(option.targetSkuCode()).isEqualTo("SKU-RED-XL");
            assertThat(option.originalSkuCode()).isEqualTo("SKU-RED-M");
            assertThat(option.quantity()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Fixtures 기반 생성 테스트")
    class FixturesTest {

        @Test
        @DisplayName("defaultExchangeOption이 올바르게 생성된다")
        void defaultExchangeOptionCreated() {
            // when
            ExchangeOption option = ExchangeFixtures.defaultExchangeOption();

            // then
            assertThat(option.originalSkuCode()).isNotBlank();
            assertThat(option.targetSkuCode()).isNotBlank();
            assertThat(option.quantity()).isGreaterThan(0);
            assertThat(option.targetProductGroupId()).isGreaterThan(0L);
            assertThat(option.targetProductId()).isGreaterThan(0L);
            assertThat(option.originalProductId()).isGreaterThan(0L);
        }
    }
}
