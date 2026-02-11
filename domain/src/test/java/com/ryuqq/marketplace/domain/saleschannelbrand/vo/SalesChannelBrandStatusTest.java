package com.ryuqq.marketplace.domain.saleschannelbrand.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandStatus Value Object 단위 테스트")
class SalesChannelBrandStatusTest {

    @Nested
    @DisplayName("상태 확인 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("ACTIVE 상태는 isActive()가 true를 반환한다")
        void activeStatusIsActive() {
            // given
            SalesChannelBrandStatus status = SalesChannelBrandStatus.ACTIVE;

            // then
            assertThat(status.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태는 isActive()가 false를 반환한다")
        void inactiveStatusIsNotActive() {
            // given
            SalesChannelBrandStatus status = SalesChannelBrandStatus.INACTIVE;

            // then
            assertThat(status.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString 변환 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 ACTIVE 상태를 생성한다")
        void fromStringWithActive() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.fromString("ACTIVE");

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효한 문자열로 INACTIVE 상태를 생성한다")
        void fromStringWithInactive() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.fromString("INACTIVE");

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.INACTIVE);
        }

        @Test
        @DisplayName("소문자 문자열로도 상태를 생성한다")
        void fromStringWithLowercase() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.fromString("active");

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.ACTIVE);
        }

        @Test
        @DisplayName("null 문자열은 기본값 ACTIVE를 반환한다")
        void fromStringWithNull_ReturnsActive() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.fromString(null);

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열은 기본값 ACTIVE를 반환한다")
        void fromStringWithBlank_ReturnsActive() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.fromString("   ");

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 문자열은 기본값 ACTIVE를 반환한다")
        void fromStringWithInvalidValue_ReturnsActive() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.fromString("INVALID");

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SalesChannelBrandStatus.values())
                    .containsExactly(
                            SalesChannelBrandStatus.ACTIVE, SalesChannelBrandStatus.INACTIVE);
        }

        @Test
        @DisplayName("valueOf()로 상태를 조회한다")
        void valueOfReturnsStatus() {
            // when
            SalesChannelBrandStatus status = SalesChannelBrandStatus.valueOf("ACTIVE");

            // then
            assertThat(status).isEqualTo(SalesChannelBrandStatus.ACTIVE);
        }
    }
}
