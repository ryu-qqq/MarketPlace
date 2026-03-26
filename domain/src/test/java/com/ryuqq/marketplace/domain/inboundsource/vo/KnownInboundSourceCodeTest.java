package com.ryuqq.marketplace.domain.inboundsource.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("KnownInboundSourceCode 단위 테스트")
class KnownInboundSourceCodeTest {

    @Nested
    @DisplayName("code() 테스트")
    class CodeTest {

        @Test
        @DisplayName("MUSTIT의 코드 문자열은 MUSTIT이다")
        void mustitCode() {
            assertThat(KnownInboundSourceCode.MUSTIT.code()).isEqualTo("MUSTIT");
        }

        @Test
        @DisplayName("SETOF의 코드 문자열은 SETOF이다")
        void setofCode() {
            assertThat(KnownInboundSourceCode.SETOF.code()).isEqualTo("SETOF");
        }
    }

    @Nested
    @DisplayName("matches() 테스트")
    class MatchesTest {

        @Test
        @DisplayName("일치하는 소스코드이면 true를 반환한다")
        void matchesReturnsTrueForMatchingCode() {
            assertThat(KnownInboundSourceCode.MUSTIT.matches("MUSTIT")).isTrue();
            assertThat(KnownInboundSourceCode.SETOF.matches("SETOF")).isTrue();
        }

        @Test
        @DisplayName("일치하지 않는 소스코드이면 false를 반환한다")
        void matchesReturnsFalseForDifferentCode() {
            assertThat(KnownInboundSourceCode.MUSTIT.matches("SETOF")).isFalse();
            assertThat(KnownInboundSourceCode.SETOF.matches("MUSTIT")).isFalse();
        }

        @Test
        @DisplayName("대소문자가 다르면 false를 반환한다")
        void matchesIsCaseSensitive() {
            assertThat(KnownInboundSourceCode.MUSTIT.matches("mustit")).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 변환한다")
        void parseValidString() {
            assertThat(KnownInboundSourceCode.fromString("MUSTIT"))
                    .isEqualTo(KnownInboundSourceCode.MUSTIT);
            assertThat(KnownInboundSourceCode.fromString("SETOF"))
                    .isEqualTo(KnownInboundSourceCode.SETOF);
        }

        @Test
        @DisplayName("소문자로도 변환 가능하다")
        void parseLowerCase() {
            assertThat(KnownInboundSourceCode.fromString("mustit"))
                    .isEqualTo(KnownInboundSourceCode.MUSTIT);
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void parseNull_ThrowsException() {
            assertThatThrownBy(() -> KnownInboundSourceCode.fromString(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void parseBlank_ThrowsException() {
            assertThatThrownBy(() -> KnownInboundSourceCode.fromString(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("알 수 없는 코드이면 예외가 발생한다")
        void parseUnknown_ThrowsException() {
            assertThatThrownBy(() -> KnownInboundSourceCode.fromString("COUPANG"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("COUPANG");
        }
    }
}
