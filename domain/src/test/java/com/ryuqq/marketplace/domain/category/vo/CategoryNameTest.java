package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryName Value Object 단위 테스트")
class CategoryNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("한글명과 영문명으로 생성한다")
        void createWithKoreanAndEnglishName() {
            CategoryName name = CategoryName.of("패션", "Fashion");

            assertThat(name.nameKo()).isEqualTo("패션");
            assertThat(name.nameEn()).isEqualTo("Fashion");
        }

        @Test
        @DisplayName("한글명만으로 생성한다 (영문명 null 허용)")
        void createWithKoreanNameOnly() {
            CategoryName name = CategoryName.of("패션", null);

            assertThat(name.nameKo()).isEqualTo("패션");
            assertThat(name.nameEn()).isNull();
        }

        @Test
        @DisplayName("ofKorean 팩토리로 한글명만 생성한다")
        void createWithOfKorean() {
            CategoryName name = CategoryName.ofKorean("패션");

            assertThat(name.nameKo()).isEqualTo("패션");
            assertThat(name.nameEn()).isNull();
        }

        @Test
        @DisplayName("앞뒤 공백은 trim된다")
        void createWithWhitespaceTrimmed() {
            CategoryName name = CategoryName.of("  패션  ", "  Fashion  ");

            assertThat(name.nameKo()).isEqualTo("패션");
            assertThat(name.nameEn()).isEqualTo("Fashion");
        }

        @Test
        @DisplayName("한글명이 null이면 예외가 발생한다")
        void createWithNullKoreanName_ThrowsException() {
            assertThatThrownBy(() -> CategoryName.of(null, "Fashion"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("한글 카테고리명");
        }

        @Test
        @DisplayName("한글명이 빈 문자열이면 예외가 발생한다")
        void createWithBlankKoreanName_ThrowsException() {
            assertThatThrownBy(() -> CategoryName.of("", "Fashion"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("한글명이 255자 초과이면 예외가 발생한다")
        void createWithTooLongKoreanName_ThrowsException() {
            String longName = "가".repeat(256);
            assertThatThrownBy(() -> CategoryName.of(longName, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("255자");
        }

        @Test
        @DisplayName("영문명이 255자 초과이면 예외가 발생한다")
        void createWithTooLongEnglishName_ThrowsException() {
            String longName = "A".repeat(256);
            assertThatThrownBy(() -> CategoryName.of("패션", longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("255자");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            CategoryName name1 = CategoryName.of("패션", "Fashion");
            CategoryName name2 = CategoryName.of("패션", "Fashion");

            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("한글명이 다르면 동일하지 않다")
        void differentKoreanNameAreNotEqual() {
            CategoryName name1 = CategoryName.of("패션", "Fashion");
            CategoryName name2 = CategoryName.of("신발", "Fashion");

            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
