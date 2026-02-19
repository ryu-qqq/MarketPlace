package com.ryuqq.marketplace.domain.selleraddress.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AddressName VO 테스트")
class AddressNameTest {

    @Nested
    @DisplayName("생성 및 검증")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            AddressName name = AddressName.of("본사 창고");
            assertThat(name.value()).isEqualTo("본사 창고");
            assertThat(name.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("앞뒤 공백을 제거한다")
        void trimWhitespace() {
            AddressName name = AddressName.of("  본사 창고  ");
            assertThat(name.value()).isEqualTo("본사 창고");
        }

        @Test
        @DisplayName("null이면 value가 null이고 isEmpty가 true이다")
        void createWithNull() {
            AddressName name = AddressName.of(null);
            assertThat(name.value()).isNull();
            assertThat(name.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("빈 문자열이면 null로 변환된다")
        void createWithBlank() {
            AddressName name = AddressName.of("   ");
            assertThat(name.value()).isNull();
            assertThat(name.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("50자 초과이면 예외가 발생한다")
        void createWithTooLong_ThrowsException() {
            String longName = "가".repeat(51);
            assertThatThrownBy(() -> AddressName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자");
        }

        @Test
        @DisplayName("50자는 허용된다")
        void createWith50Chars() {
            String name = "가".repeat(50);
            AddressName addressName = AddressName.of(name);
            assertThat(addressName.value()).hasSize(50);
        }
    }

    @Nested
    @DisplayName("empty() - 빈 주소명 생성")
    class EmptyTest {

        @Test
        @DisplayName("빈 주소명을 생성한다")
        void createEmpty() {
            AddressName name = AddressName.empty();
            assertThat(name.value()).isNull();
            assertThat(name.isEmpty()).isTrue();
        }
    }
}
