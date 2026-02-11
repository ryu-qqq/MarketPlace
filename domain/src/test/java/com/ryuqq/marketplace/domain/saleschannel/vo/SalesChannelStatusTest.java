package com.ryuqq.marketplace.domain.saleschannel.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelStatus Enum 단위 테스트")
class SalesChannelStatusTest {

    @Nested
    @DisplayName("isActive 메서드 테스트")
    class IsActiveTest {
        @Test
        @DisplayName("ACTIVE 상태는 isActive()가 true를 반환한다")
        void activeStatusReturnsTrue() {
            assertThat(SalesChannelStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태는 isActive()가 false를 반환한다")
        void inactiveStatusReturnsFalse() {
            assertThat(SalesChannelStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString 메서드 테스트")
    class FromStringTest {
        @Test
        @DisplayName("유효한 문자열(ACTIVE)을 ACTIVE로 변환한다")
        void parseActiveString() {
            assertThat(SalesChannelStatus.fromString("ACTIVE"))
                    .isEqualTo(SalesChannelStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효한 문자열(INACTIVE)을 INACTIVE로 변환한다")
        void parseInactiveString() {
            assertThat(SalesChannelStatus.fromString("INACTIVE"))
                    .isEqualTo(SalesChannelStatus.INACTIVE);
        }

        @Test
        @DisplayName("소문자 문자열도 대문자로 변환하여 파싱한다")
        void parseLowercaseString() {
            assertThat(SalesChannelStatus.fromString("active"))
                    .isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(SalesChannelStatus.fromString("inactive"))
                    .isEqualTo(SalesChannelStatus.INACTIVE);
        }

        @Test
        @DisplayName("null 문자열은 ACTIVE를 기본값으로 반환한다")
        void parseNullString_ReturnsActive() {
            assertThat(SalesChannelStatus.fromString(null)).isEqualTo(SalesChannelStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열은 ACTIVE를 기본값으로 반환한다")
        void parseEmptyString_ReturnsActive() {
            assertThat(SalesChannelStatus.fromString("")).isEqualTo(SalesChannelStatus.ACTIVE);
        }

        @Test
        @DisplayName("공백 문자열은 ACTIVE를 기본값으로 반환한다")
        void parseBlankString_ReturnsActive() {
            assertThat(SalesChannelStatus.fromString("   ")).isEqualTo(SalesChannelStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 문자열은 ACTIVE를 기본값으로 반환한다")
        void parseInvalidString_ReturnsActive() {
            assertThat(SalesChannelStatus.fromString("INVALID"))
                    .isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(SalesChannelStatus.fromString("UNKNOWN"))
                    .isEqualTo(SalesChannelStatus.ACTIVE);
        }
    }
}
