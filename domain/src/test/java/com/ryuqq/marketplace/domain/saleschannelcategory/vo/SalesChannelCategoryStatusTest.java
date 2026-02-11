package com.ryuqq.marketplace.domain.saleschannelcategory.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategoryStatus Value Object 테스트")
class SalesChannelCategoryStatusTest {

    @Nested
    @DisplayName("isActive() 메서드 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE 상태는 true를 반환한다")
        void activeReturnsTrue() {
            // when & then
            assertThat(SalesChannelCategoryStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태는 false를 반환한다")
        void inactiveReturnsFalse() {
            // when & then
            assertThat(SalesChannelCategoryStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 메서드 테스트")
    class FromStringTest {

        @Test
        @DisplayName("대문자 문자열로 ACTIVE를 생성한다")
        void createActiveFromUpperCaseString() {
            // when
            SalesChannelCategoryStatus status = SalesChannelCategoryStatus.fromString("ACTIVE");

            // then
            assertThat(status).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("소문자 문자열로 ACTIVE를 생성한다")
        void createActiveFromLowerCaseString() {
            // when
            SalesChannelCategoryStatus status = SalesChannelCategoryStatus.fromString("active");

            // then
            assertThat(status).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("대문자 문자열로 INACTIVE를 생성한다")
        void createInactiveFromUpperCaseString() {
            // when
            SalesChannelCategoryStatus status = SalesChannelCategoryStatus.fromString("INACTIVE");

            // then
            assertThat(status).isEqualTo(SalesChannelCategoryStatus.INACTIVE);
        }

        @Test
        @DisplayName("null이면 ACTIVE를 반환한다")
        void nullReturnsActive() {
            // when
            SalesChannelCategoryStatus status = SalesChannelCategoryStatus.fromString(null);

            // then
            assertThat(status).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열이면 ACTIVE를 반환한다")
        void blankStringReturnsActive() {
            // when
            SalesChannelCategoryStatus status = SalesChannelCategoryStatus.fromString("   ");

            // then
            assertThat(status).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 값이면 ACTIVE를 반환한다")
        void invalidValueReturnsActive() {
            // when
            SalesChannelCategoryStatus status = SalesChannelCategoryStatus.fromString("INVALID");

            // then
            assertThat(status).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SalesChannelCategoryStatus.values())
                    .containsExactly(
                            SalesChannelCategoryStatus.ACTIVE, SalesChannelCategoryStatus.INACTIVE);
        }
    }
}
