package com.ryuqq.marketplace.domain.canonicaloption.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionValueName Value Object 단위 테스트")
class CanonicalOptionValueNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("한국어 이름과 영어 이름으로 생성한다")
        void createWithKoreanAndEnglishNames() {
            // given & when
            CanonicalOptionValueName name = CanonicalOptionValueName.of("검정색", "Black");

            // then
            assertThat(name.nameKo()).isEqualTo("검정색");
            assertThat(name.nameEn()).isEqualTo("Black");
        }

        @Test
        @DisplayName("한국어 이름만으로 생성한다 (영어 이름 null)")
        void createWithKoreanNameOnly() {
            // given & when
            CanonicalOptionValueName name = CanonicalOptionValueName.of("검정색", null);

            // then
            assertThat(name.nameKo()).isEqualTo("검정색");
            assertThat(name.nameEn()).isNull();
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // when
            CanonicalOptionValueName name = CanonicalOptionValueName.of("  검정색  ", "  Black  ");

            // then
            assertThat(name.nameKo()).isEqualTo("검정색");
            assertThat(name.nameEn()).isEqualTo("Black");
        }

        @Test
        @DisplayName("한국어 이름이 null이면 예외가 발생한다")
        void nullKoreanNameThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueName.of(null, "Black"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 빈 문자열이면 예외가 발생한다")
        void emptyKoreanNameThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueName.of("", "Black"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 공백만 있으면 예외가 발생한다")
        void blankKoreanNameThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueName.of("   ", "Black"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 100자를 초과하면 예외가 발생한다")
        void koreanNameTooLongThrowsException() {
            // given
            String longName = "가".repeat(101);

            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueName.of(longName, "Black"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("영어 이름이 100자를 초과하면 예외가 발생한다")
        void englishNameTooLongThrowsException() {
            // given
            String longName = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> CanonicalOptionValueName.of("검정색", longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("한국어 이름과 영어 이름 모두 100자는 허용된다")
        void maxLengthIsAllowed() {
            // given
            String maxKoreanName = "가".repeat(100);
            String maxEnglishName = "a".repeat(100);

            // when
            CanonicalOptionValueName name =
                    CanonicalOptionValueName.of(maxKoreanName, maxEnglishName);

            // then
            assertThat(name.nameKo()).hasSize(100);
            assertThat(name.nameEn()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            CanonicalOptionValueName name1 = CanonicalOptionValueName.of("검정색", "Black");
            CanonicalOptionValueName name2 = CanonicalOptionValueName.of("검정색", "Black");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            // given
            CanonicalOptionValueName name1 = CanonicalOptionValueName.of("검정색", "Black");
            CanonicalOptionValueName name2 = CanonicalOptionValueName.of("흰색", "White");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record 타입이므로 불변이다")
        void isImmutable() {
            // given
            CanonicalOptionValueName name = CanonicalOptionValueName.of("검정색", "Black");

            // then
            assertThat(name.nameKo()).isEqualTo("검정색");
            assertThat(name.nameEn()).isEqualTo("Black");
            // record는 final 클래스이므로 값 변경 불가능
        }
    }
}
