package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("CategoryStatus 단위 테스트")
class CategoryStatusTest {

    @Nested
    @DisplayName("isActive() - 활성 여부")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE는 활성 상태다")
        void activeIsActive() {
            assertThat(CategoryStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 활성 상태가 아니다")
        void inactiveIsNotActive() {
            assertThat(CategoryStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() - 문자열 변환")
    class FromStringTest {

        @Test
        @DisplayName("'ACTIVE' 문자열은 ACTIVE로 변환된다")
        void activeStringConvertsToActive() {
            assertThat(CategoryStatus.fromString("ACTIVE")).isEqualTo(CategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("'inactive' 소문자도 INACTIVE로 변환된다")
        void lowercaseInactiveConvertsToInactive() {
            assertThat(CategoryStatus.fromString("inactive")).isEqualTo(CategoryStatus.INACTIVE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("null, 빈 문자열, 공백은 ACTIVE(기본값)로 변환된다")
        void nullOrBlankReturnsActive(String value) {
            assertThat(CategoryStatus.fromString(value)).isEqualTo(CategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 문자열은 ACTIVE(기본값)로 변환된다")
        void invalidStringReturnsActive() {
            assertThat(CategoryStatus.fromString("INVALID")).isEqualTo(CategoryStatus.ACTIVE);
        }
    }

    @Test
    @DisplayName("CategoryStatus 값이 2개 존재한다")
    void hasExpectedValues() {
        assertThat(CategoryStatus.values()).hasSize(2);
    }
}
