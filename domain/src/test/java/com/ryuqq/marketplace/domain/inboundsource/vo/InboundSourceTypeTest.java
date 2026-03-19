package com.ryuqq.marketplace.domain.inboundsource.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundSourceType 단위 테스트")
class InboundSourceTypeTest {

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 타입을 변환한다")
        void parseValidStrings() {
            assertThat(InboundSourceType.fromString("CRAWLING"))
                    .isEqualTo(InboundSourceType.CRAWLING);
            assertThat(InboundSourceType.fromString("LEGACY")).isEqualTo(InboundSourceType.LEGACY);
            assertThat(InboundSourceType.fromString("PARTNER"))
                    .isEqualTo(InboundSourceType.PARTNER);
        }

        @Test
        @DisplayName("소문자로도 변환 가능하다")
        void parseLowerCase() {
            assertThat(InboundSourceType.fromString("crawling"))
                    .isEqualTo(InboundSourceType.CRAWLING);
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void parseNull_ThrowsException() {
            assertThatThrownBy(() -> InboundSourceType.fromString(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void parseBlank_ThrowsException() {
            assertThatThrownBy(() -> InboundSourceType.fromString(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 예외가 발생한다")
        void parseInvalid_ThrowsException() {
            assertThatThrownBy(() -> InboundSourceType.fromString("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("INVALID");
        }
    }
}
