package com.ryuqq.marketplace.domain.inboundsource.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundSourceStatus 단위 테스트")
class InboundSourceStatusTest {

    @Nested
    @DisplayName("isActive() 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE 상태는 isActive()가 true이다")
        void activeIsActive() {
            assertThat(InboundSourceStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태는 isActive()가 false이다")
        void inactiveIsNotActive() {
            assertThat(InboundSourceStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열 ACTIVE로 변환한다")
        void parseActive() {
            assertThat(InboundSourceStatus.fromString("ACTIVE"))
                    .isEqualTo(InboundSourceStatus.ACTIVE);
        }

        @Test
        @DisplayName("소문자로도 변환 가능하다")
        void parseLowerCase() {
            assertThat(InboundSourceStatus.fromString("inactive"))
                    .isEqualTo(InboundSourceStatus.INACTIVE);
        }

        @Test
        @DisplayName("null이면 기본값 ACTIVE를 반환한다")
        void parseNull_ReturnsActive() {
            assertThat(InboundSourceStatus.fromString(null)).isEqualTo(InboundSourceStatus.ACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 기본값 ACTIVE를 반환한다")
        void parseInvalid_ReturnsActive() {
            assertThat(InboundSourceStatus.fromString("UNKNOWN"))
                    .isEqualTo(InboundSourceStatus.ACTIVE);
        }
    }
}
