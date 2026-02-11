package com.ryuqq.marketplace.domain.canonicaloption.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupName Value Object 단위 테스트")
class CanonicalOptionGroupNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("한국어 이름과 영어 이름으로 생성한다")
        void createWithKoreanAndEnglishNames() {
            // given & when
            CanonicalOptionGroupName name = CanonicalOptionGroupName.of("색상", "Color");

            // then
            assertThat(name.nameKo()).isEqualTo("색상");
            assertThat(name.nameEn()).isEqualTo("Color");
        }

        @Test
        @DisplayName("한국어 이름만으로 생성한다 (영어 이름 null)")
        void createWithKoreanNameOnly() {
            // given & when
            CanonicalOptionGroupName name = CanonicalOptionGroupName.of("색상", null);

            // then
            assertThat(name.nameKo()).isEqualTo("색상");
            assertThat(name.nameEn()).isNull();
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // when
            CanonicalOptionGroupName name = CanonicalOptionGroupName.of("  색상  ", "  Color  ");

            // then
            assertThat(name.nameKo()).isEqualTo("색상");
            assertThat(name.nameEn()).isEqualTo("Color");
        }

        @Test
        @DisplayName("한국어 이름이 null이면 예외가 발생한다")
        void nullKoreanNameThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupName.of(null, "Color"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 빈 문자열이면 예외가 발생한다")
        void emptyKoreanNameThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupName.of("", "Color"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 공백만 있으면 예외가 발생한다")
        void blankKoreanNameThrowsException() {
            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupName.of("   ", "Color"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 100자를 초과하면 예외가 발생한다")
        void koreanNameTooLongThrowsException() {
            // given
            String longName = "가".repeat(101);

            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupName.of(longName, "Color"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("영어 이름이 100자를 초과하면 예외가 발생한다")
        void englishNameTooLongThrowsException() {
            // given
            String longName = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> CanonicalOptionGroupName.of("색상", longName))
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
            CanonicalOptionGroupName name =
                    CanonicalOptionGroupName.of(maxKoreanName, maxEnglishName);

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
            CanonicalOptionGroupName name1 = CanonicalOptionGroupName.of("색상", "Color");
            CanonicalOptionGroupName name2 = CanonicalOptionGroupName.of("색상", "Color");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            // given
            CanonicalOptionGroupName name1 = CanonicalOptionGroupName.of("색상", "Color");
            CanonicalOptionGroupName name2 = CanonicalOptionGroupName.of("사이즈", "Size");

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
            CanonicalOptionGroupName name = CanonicalOptionGroupName.of("색상", "Color");

            // then
            assertThat(name.nameKo()).isEqualTo("색상");
            assertThat(name.nameEn()).isEqualTo("Color");
            // record는 final 클래스이므로 값 변경 불가능
        }
    }
}
