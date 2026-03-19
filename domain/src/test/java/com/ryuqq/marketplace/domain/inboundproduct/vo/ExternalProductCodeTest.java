package com.ryuqq.marketplace.domain.inboundproduct.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalProductCode 단위 테스트")
class ExternalProductCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 코드 문자열로 생성한다")
        void createWithValidValue() {
            ExternalProductCode code = ExternalProductCode.of("EXT-PROD-001");

            assertThat(code.value()).isEqualTo("EXT-PROD-001");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> ExternalProductCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> ExternalProductCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            assertThatThrownBy(() -> ExternalProductCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("동일한 코드는 같다")
        void sameCodeAreEqual() {
            ExternalProductCode code1 = ExternalProductCode.of("EXT-001");
            ExternalProductCode code2 = ExternalProductCode.of("EXT-001");

            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 코드는 같지 않다")
        void differentCodesAreNotEqual() {
            ExternalProductCode code1 = ExternalProductCode.of("EXT-001");
            ExternalProductCode code2 = ExternalProductCode.of("EXT-002");

            assertThat(code1).isNotEqualTo(code2);
        }
    }
}
