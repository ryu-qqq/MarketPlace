package com.ryuqq.marketplace.domain.categorypreset.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetStatus VO 테스트")
class CategoryPresetStatusTest {

    @Nested
    @DisplayName("isActive() 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE는 isActive() true를 반환한다")
        void activeIsActive() {
            assertThat(CategoryPresetStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 isActive() false를 반환한다")
        void inactiveIsNotActive() {
            assertThat(CategoryPresetStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 상태를 변환한다")
        void convertValidString() {
            assertThat(CategoryPresetStatus.fromString("ACTIVE"))
                    .isEqualTo(CategoryPresetStatus.ACTIVE);
            assertThat(CategoryPresetStatus.fromString("INACTIVE"))
                    .isEqualTo(CategoryPresetStatus.INACTIVE);
        }

        @Test
        @DisplayName("대소문자를 무시하고 변환한다")
        void convertCaseInsensitive() {
            assertThat(CategoryPresetStatus.fromString("active"))
                    .isEqualTo(CategoryPresetStatus.ACTIVE);
        }

        @Test
        @DisplayName("null이면 ACTIVE를 반환한다")
        void nullReturnsActive() {
            assertThat(CategoryPresetStatus.fromString(null))
                    .isEqualTo(CategoryPresetStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열이면 ACTIVE를 반환한다")
        void blankReturnsActive() {
            assertThat(CategoryPresetStatus.fromString("")).isEqualTo(CategoryPresetStatus.ACTIVE);
            assertThat(CategoryPresetStatus.fromString("   "))
                    .isEqualTo(CategoryPresetStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 ACTIVE를 반환한다")
        void invalidStringReturnsActive() {
            assertThat(CategoryPresetStatus.fromString("UNKNOWN"))
                    .isEqualTo(CategoryPresetStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allValuesExist() {
            assertThat(CategoryPresetStatus.values())
                    .containsExactly(CategoryPresetStatus.ACTIVE, CategoryPresetStatus.INACTIVE);
        }
    }
}
