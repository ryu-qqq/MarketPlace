package com.ryuqq.marketplace.domain.selleradmin.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminName Value Object 단위 테스트")
class AdminNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 이름으로 생성한다")
        void createWithValidName() {
            AdminName name = AdminName.of("홍길동");

            assertThat(name.value()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("앞뒤 공백은 trim된다")
        void createWithWhitespaceTrimmed() {
            AdminName name = AdminName.of("  홍길동  ");

            assertThat(name.value()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> AdminName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> AdminName.of("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            assertThatThrownBy(() -> AdminName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("50자 초과이면 예외가 발생한다")
        void createWithTooLong_ThrowsException() {
            String longName = "가".repeat(51);
            assertThatThrownBy(() -> AdminName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자");
        }

        @Test
        @DisplayName("정확히 50자인 이름으로 생성한다")
        void createWith50CharName() {
            String name50 = "가".repeat(50);
            AdminName name = AdminName.of(name50);

            assertThat(name.value()).isEqualTo(name50);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            AdminName name1 = AdminName.of("홍길동");
            AdminName name2 = AdminName.of("홍길동");

            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            AdminName name1 = AdminName.of("홍길동");
            AdminName name2 = AdminName.of("김길동");

            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
