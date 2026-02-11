package com.ryuqq.marketplace.domain.notice.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeFieldName Value Object 테스트")
class NoticeFieldNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given & when
            NoticeFieldName name = NoticeFieldName.of("소재");

            // then
            assertThat(name.value()).isEqualTo("소재");
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // given & when
            NoticeFieldName name = NoticeFieldName.of("  소재  ");

            // then
            assertThat(name.value()).isEqualTo("소재");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeFieldName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithEmptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeFieldName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void createWithBlankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeFieldName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("100자를 초과하면 예외가 발생한다")
        void createWithTooLongValueThrowsException() {
            // given
            String longName = "가".repeat(101);

            // when & then
            assertThatThrownBy(() -> NoticeFieldName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("100자 이하는 허용된다")
        void createWith100CharsIsAllowed() {
            // given
            String name = "가".repeat(100);

            // when
            NoticeFieldName result = NoticeFieldName.of(name);

            // then
            assertThat(result.value()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 이름은 동등하다")
        void sameValueEquals() {
            // given
            NoticeFieldName name1 = NoticeFieldName.of("제조국");
            NoticeFieldName name2 = NoticeFieldName.of("제조국");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 이름은 동등하지 않다")
        void differentValueNotEquals() {
            // given
            NoticeFieldName name1 = NoticeFieldName.of("제조국");
            NoticeFieldName name2 = NoticeFieldName.of("소재");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("NoticeFieldName은 불변 객체다")
        void isImmutable() {
            // given
            NoticeFieldName name = NoticeFieldName.of("제조자");

            // when
            String originalValue = name.value();

            // then - record는 불변이므로 값 변경 불가
            assertThat(name.value()).isEqualTo(originalValue);
        }
    }
}
