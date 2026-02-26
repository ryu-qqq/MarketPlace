package com.ryuqq.marketplace.domain.shop.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopStatus Value Object 단위 테스트")
class ShopStatusTest {

    @Nested
    @DisplayName("isActive() 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE 상태는 isActive()가 true를 반환한다")
        void activeStatusIsActive() {
            // given
            ShopStatus status = ShopStatus.ACTIVE;

            // then
            assertThat(status.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태는 isActive()가 false를 반환한다")
        void inactiveStatusIsNotActive() {
            // given
            ShopStatus status = ShopStatus.INACTIVE;

            // then
            assertThat(status.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("'ACTIVE' 문자열로 ACTIVE 상태를 생성한다")
        void createActiveFromString() {
            // when
            ShopStatus status = ShopStatus.fromString("ACTIVE");

            // then
            assertThat(status).isEqualTo(ShopStatus.ACTIVE);
        }

        @Test
        @DisplayName("'active' 소문자 문자열로 ACTIVE 상태를 생성한다")
        void createActiveFromLowercaseString() {
            // when
            ShopStatus status = ShopStatus.fromString("active");

            // then
            assertThat(status).isEqualTo(ShopStatus.ACTIVE);
        }

        @Test
        @DisplayName("'INACTIVE' 문자열로 INACTIVE 상태를 생성한다")
        void createInactiveFromString() {
            // when
            ShopStatus status = ShopStatus.fromString("INACTIVE");

            // then
            assertThat(status).isEqualTo(ShopStatus.INACTIVE);
        }

        @Test
        @DisplayName("'inactive' 소문자 문자열로 INACTIVE 상태를 생성한다")
        void createInactiveFromLowercaseString() {
            // when
            ShopStatus status = ShopStatus.fromString("inactive");

            // then
            assertThat(status).isEqualTo(ShopStatus.INACTIVE);
        }

        @Test
        @DisplayName("null 문자열은 ACTIVE를 반환한다(기본값)")
        void createFromNullReturnsActive() {
            // when
            ShopStatus status = ShopStatus.fromString(null);

            // then
            assertThat(status).isEqualTo(ShopStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열은 ACTIVE를 반환한다(기본값)")
        void createFromBlankReturnsActive() {
            // when
            ShopStatus status = ShopStatus.fromString("   ");

            // then
            assertThat(status).isEqualTo(ShopStatus.ACTIVE);
        }

        @Test
        @DisplayName("잘못된 문자열은 ACTIVE를 반환한다(기본값)")
        void createFromInvalidStringReturnsActive() {
            // when
            ShopStatus status = ShopStatus.fromString("INVALID_STATUS");

            // then
            assertThat(status).isEqualTo(ShopStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 ShopStatus 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ShopStatus.values()).containsExactly(ShopStatus.ACTIVE, ShopStatus.INACTIVE);
        }

        @Test
        @DisplayName("valueOf()로 ACTIVE 상태를 가져온다")
        void valueOfActive() {
            // when
            ShopStatus status = ShopStatus.valueOf("ACTIVE");

            // then
            assertThat(status).isEqualTo(ShopStatus.ACTIVE);
        }

        @Test
        @DisplayName("valueOf()로 INACTIVE 상태를 가져온다")
        void valueOfInactive() {
            // when
            ShopStatus status = ShopStatus.valueOf("INACTIVE");

            // then
            assertThat(status).isEqualTo(ShopStatus.INACTIVE);
        }
    }
}
