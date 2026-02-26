package com.ryuqq.marketplace.domain.brandmapping.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandMappingStatus VO 테스트")
class BrandMappingStatusTest {

    @Nested
    @DisplayName("isActive() 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE는 isActive() true를 반환한다")
        void activeIsActive() {
            assertThat(BrandMappingStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 isActive() false를 반환한다")
        void inactiveIsNotActive() {
            assertThat(BrandMappingStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 상태를 변환한다")
        void convertValidString() {
            assertThat(BrandMappingStatus.fromString("ACTIVE"))
                    .isEqualTo(BrandMappingStatus.ACTIVE);
            assertThat(BrandMappingStatus.fromString("INACTIVE"))
                    .isEqualTo(BrandMappingStatus.INACTIVE);
        }

        @Test
        @DisplayName("대소문자를 무시하고 변환한다")
        void convertCaseInsensitive() {
            assertThat(BrandMappingStatus.fromString("active"))
                    .isEqualTo(BrandMappingStatus.ACTIVE);
        }

        @Test
        @DisplayName("null이면 ACTIVE를 반환한다")
        void nullReturnsActive() {
            assertThat(BrandMappingStatus.fromString(null)).isEqualTo(BrandMappingStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열이면 ACTIVE를 반환한다")
        void blankReturnsActive() {
            assertThat(BrandMappingStatus.fromString("")).isEqualTo(BrandMappingStatus.ACTIVE);
            assertThat(BrandMappingStatus.fromString("   ")).isEqualTo(BrandMappingStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 ACTIVE를 반환한다")
        void invalidStringReturnsActive() {
            assertThat(BrandMappingStatus.fromString("UNKNOWN"))
                    .isEqualTo(BrandMappingStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allValuesExist() {
            assertThat(BrandMappingStatus.values())
                    .containsExactly(BrandMappingStatus.ACTIVE, BrandMappingStatus.INACTIVE);
        }
    }
}
