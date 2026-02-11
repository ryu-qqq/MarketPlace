package com.ryuqq.marketplace.domain.product.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SkuCode Value Object 테스트")
class SkuCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 SKU 코드를 생성한다")
        void createWithValidValue() {
            // given
            String validSkuCode = "SKU-TEST-001";

            // when
            SkuCode skuCode = SkuCode.of(validSkuCode);

            // then
            assertThat(skuCode.value()).isEqualTo(validSkuCode);
        }

        @Test
        @DisplayName("null 값으로 SKU 코드를 생성한다")
        void createWithNullValue() {
            // when
            SkuCode skuCode = SkuCode.of(null);

            // then
            assertThat(skuCode.value()).isNull();
            assertThat(skuCode.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("100자를 초과하는 SKU 코드는 예외를 발생시킨다")
        void createWithOver100CharactersThrowsException() {
            // given
            String longSkuCode = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> SkuCode.of(longSkuCode))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자를 초과");
        }

        @Test
        @DisplayName("정확히 100자의 SKU 코드는 생성 가능하다")
        void createWith100CharactersSucceeds() {
            // given
            String validSkuCode = "A".repeat(100);

            // when
            SkuCode skuCode = SkuCode.of(validSkuCode);

            // then
            assertThat(skuCode.value()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("isEmpty() - 비어있음 확인")
    class IsEmptyTest {

        @Test
        @DisplayName("null 값이면 isEmpty()는 true를 반환한다")
        void isEmptyReturnsTrueWhenNull() {
            // given
            SkuCode skuCode = SkuCode.of(null);

            // then
            assertThat(skuCode.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("빈 문자열이면 isEmpty()는 true를 반환한다")
        void isEmptyReturnsTrueWhenBlank() {
            // given
            SkuCode skuCode = SkuCode.of("   ");

            // then
            assertThat(skuCode.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("값이 있으면 isEmpty()는 false를 반환한다")
        void isEmptyReturnsFalseWhenHasValue() {
            // given
            SkuCode skuCode = SkuCode.of("SKU-001");

            // then
            assertThat(skuCode.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 SkuCode는 동등하다")
        void sameValueEquals() {
            // given
            SkuCode skuCode1 = SkuCode.of("SKU-001");
            SkuCode skuCode2 = SkuCode.of("SKU-001");

            // then
            assertThat(skuCode1).isEqualTo(skuCode2);
            assertThat(skuCode1.hashCode()).isEqualTo(skuCode2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 SkuCode는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            SkuCode skuCode1 = SkuCode.of("SKU-001");
            SkuCode skuCode2 = SkuCode.of("SKU-002");

            // then
            assertThat(skuCode1).isNotEqualTo(skuCode2);
        }

        @Test
        @DisplayName("null 값을 가진 SkuCode들은 동등하다")
        void nullValueEquals() {
            // given
            SkuCode skuCode1 = SkuCode.of(null);
            SkuCode skuCode2 = SkuCode.of(null);

            // then
            assertThat(skuCode1).isEqualTo(skuCode2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("SkuCode는 불변 객체이다")
        void skuCodeIsImmutable() {
            // given
            String originalValue = "SKU-001";
            SkuCode skuCode = SkuCode.of(originalValue);

            // when
            String retrievedValue = skuCode.value();

            // then
            assertThat(retrievedValue).isEqualTo(originalValue);
            assertThat(skuCode.value()).isEqualTo(originalValue); // 여러 번 호출해도 같은 값
        }
    }
}
