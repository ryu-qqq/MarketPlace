package com.ryuqq.marketplace.domain.notice.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeCategoryName Value Object 테스트")
class NoticeCategoryNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("한국어와 영어 이름으로 생성한다")
        void createWithKoreanAndEnglish() {
            // given & when
            NoticeCategoryName name = NoticeCategoryName.of("의류", "Clothing");

            // then
            assertThat(name.nameKo()).isEqualTo("의류");
            assertThat(name.nameEn()).isEqualTo("Clothing");
        }

        @Test
        @DisplayName("한국어 이름만으로 생성한다")
        void createWithKoreanOnly() {
            // given & when
            NoticeCategoryName name = NoticeCategoryName.ofKorean("전자제품");

            // then
            assertThat(name.nameKo()).isEqualTo("전자제품");
            assertThat(name.nameEn()).isNull();
        }

        @Test
        @DisplayName("한국어 이름 공백이 트림된다")
        void koreanNameIsTrimmed() {
            // given & when
            NoticeCategoryName name = NoticeCategoryName.of("  의류  ", "Clothing");

            // then
            assertThat(name.nameKo()).isEqualTo("의류");
        }

        @Test
        @DisplayName("영어 이름 공백이 트림된다")
        void englishNameIsTrimmed() {
            // given & when
            NoticeCategoryName name = NoticeCategoryName.of("의류", "  Clothing  ");

            // then
            assertThat(name.nameEn()).isEqualTo("Clothing");
        }

        @Test
        @DisplayName("한국어 이름이 null이면 예외가 발생한다")
        void createWithNullKoreanThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeCategoryName.of(null, "Clothing"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 빈 문자열이면 예외가 발생한다")
        void createWithEmptyKoreanThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeCategoryName.of("", "Clothing"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 공백만 있으면 예외가 발생한다")
        void createWithBlankKoreanThrowsException() {
            // when & then
            assertThatThrownBy(() -> NoticeCategoryName.of("   ", "Clothing"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("한국어 이름이 100자를 초과하면 예외가 발생한다")
        void createWithTooLongKoreanThrowsException() {
            // given
            String longName = "가".repeat(101);

            // when & then
            assertThatThrownBy(() -> NoticeCategoryName.of(longName, "Clothing"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("영어 이름이 100자를 초과하면 예외가 발생한다")
        void createWithTooLongEnglishThrowsException() {
            // given
            String longName = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> NoticeCategoryName.of("의류", longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }

        @Test
        @DisplayName("영어 이름은 null이 허용된다")
        void englishNameCanBeNull() {
            // when
            NoticeCategoryName name = NoticeCategoryName.of("의류", null);

            // then
            assertThat(name.nameKo()).isEqualTo("의류");
            assertThat(name.nameEn()).isNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 이름은 동등하다")
        void sameValueEquals() {
            // given
            NoticeCategoryName name1 = NoticeCategoryName.of("식품", "Food");
            NoticeCategoryName name2 = NoticeCategoryName.of("식품", "Food");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("한국어 이름이 다르면 동등하지 않다")
        void differentKoreanNotEquals() {
            // given
            NoticeCategoryName name1 = NoticeCategoryName.of("식품", "Food");
            NoticeCategoryName name2 = NoticeCategoryName.of("의류", "Food");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("영어 이름이 다르면 동등하지 않다")
        void differentEnglishNotEquals() {
            // given
            NoticeCategoryName name1 = NoticeCategoryName.of("식품", "Food");
            NoticeCategoryName name2 = NoticeCategoryName.of("식품", "Clothing");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("NoticeCategoryName은 불변 객체다")
        void isImmutable() {
            // given
            NoticeCategoryName name = NoticeCategoryName.of("전자제품", "Electronics");

            // when
            String originalKo = name.nameKo();
            String originalEn = name.nameEn();

            // then - record는 불변이므로 값 변경 불가
            assertThat(name.nameKo()).isEqualTo(originalKo);
            assertThat(name.nameEn()).isEqualTo(originalEn);
        }
    }
}
