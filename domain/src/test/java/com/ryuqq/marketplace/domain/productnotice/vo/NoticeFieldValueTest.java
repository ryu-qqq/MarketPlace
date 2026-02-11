package com.ryuqq.marketplace.domain.productnotice.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeFieldValue Value Object 테스트")
class NoticeFieldValueTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 NoticeFieldValue를 생성한다")
        void createWithValidValue() {
            // when
            NoticeFieldValue value = NoticeFieldValue.of("유효한 고시정보 값");

            // then
            assertThat(value.value()).isEqualTo("유효한 고시정보 값");
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외를 발생시킨다")
        void createWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeFieldValue.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비어있을 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외를 발생시킨다")
        void createWithEmptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeFieldValue.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비어있을 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있는 문자열로 생성하면 예외를 발생시킨다")
        void createWithBlankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeFieldValue.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비어있을 수 없습니다");
        }

        @Test
        @DisplayName("500자를 초과하는 값으로 생성하면 예외를 발생시킨다")
        void createWithTooLongValueThrowsException() {
            // given
            String tooLongValue = "a".repeat(501);

            // when & then
            assertThatThrownBy(() -> NoticeFieldValue.of(tooLongValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("500자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("정확히 500자인 값으로 생성할 수 있다")
        void createWithExactly500Characters() {
            // given
            String maxLengthValue = "a".repeat(500);

            // when
            NoticeFieldValue value = NoticeFieldValue.of(maxLengthValue);

            // then
            assertThat(value.value()).hasSize(500);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 NoticeFieldValue는 동등하다")
        void sameValueEquals() {
            // given
            NoticeFieldValue value1 = NoticeFieldValue.of("테스트 값");
            NoticeFieldValue value2 = NoticeFieldValue.of("테스트 값");

            // then
            assertThat(value1).isEqualTo(value2);
            assertThat(value1.hashCode()).isEqualTo(value2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 NoticeFieldValue는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            NoticeFieldValue value1 = NoticeFieldValue.of("값1");
            NoticeFieldValue value2 = NoticeFieldValue.of("값2");

            // then
            assertThat(value1).isNotEqualTo(value2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("NoticeFieldValue는 불변 객체이다")
        void valueObjectIsImmutable() {
            // given
            String originalValue = "원본 값";
            NoticeFieldValue value = NoticeFieldValue.of(originalValue);

            // when & then
            assertThat(value.value()).isEqualTo(originalValue);
            // record는 불변이므로 setter가 없음
        }
    }
}
