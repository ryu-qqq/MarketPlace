package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BuyerName Value Object 테스트")
class BuyerNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 이름으로 BuyerName을 생성한다")
        void createWithValidValue() {
            // when
            BuyerName buyerName = BuyerName.of("홍길동");

            // then
            assertThat(buyerName.value()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> BuyerName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("구매자 이름은 필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> BuyerName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("구매자 이름은 필수");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 이름의 BuyerName은 동일하다")
        void sameValuesAreEqual() {
            // when
            BuyerName name1 = BuyerName.of("홍길동");
            BuyerName name2 = BuyerName.of("홍길동");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 이름의 BuyerName은 동일하지 않다")
        void differentValuesAreNotEqual() {
            // when
            BuyerName name1 = BuyerName.of("홍길동");
            BuyerName name2 = BuyerName.of("김철수");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
