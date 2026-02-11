package com.ryuqq.marketplace.domain.channeloptionmapping.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalOptionCode Value Object 테스트")
class ExternalOptionCodeTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 ExternalOptionCode를 생성한다")
        void createWithValidValue() {
            // given & when
            ExternalOptionCode code = ExternalOptionCode.of("EXT-OPTION-001");

            // then
            assertThat(code.value()).isEqualTo("EXT-OPTION-001");
        }

        @Test
        @DisplayName("null로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExternalOptionCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 옵션 코드는 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithEmptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExternalOptionCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 옵션 코드는 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("공백 문자열로 생성하면 예외가 발생한다")
        void createWithBlankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> ExternalOptionCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 옵션 코드는 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("100자를 초과하는 값으로 생성하면 예외가 발생한다")
        void createWithTooLongValueThrowsException() {
            // given
            String tooLongValue = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> ExternalOptionCode.of(tooLongValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 옵션 코드는 100자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("정확히 100자인 값으로 생성할 수 있다")
        void createWithExactly100Characters() {
            // given
            String value100 = "A".repeat(100);

            // when & then
            ExternalOptionCode code = ExternalOptionCode.of(value100);
            assertThat(code.value()).hasSize(100);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 ExternalOptionCode는 동등하다")
        void sameValueEquals() {
            // given
            ExternalOptionCode code1 = ExternalOptionCode.of("CODE-001");
            ExternalOptionCode code2 = ExternalOptionCode.of("CODE-001");

            // then
            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ExternalOptionCode는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            ExternalOptionCode code1 = ExternalOptionCode.of("CODE-001");
            ExternalOptionCode code2 = ExternalOptionCode.of("CODE-002");

            // then
            assertThat(code1).isNotEqualTo(code2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ExternalOptionCode는 불변 객체다")
        void isImmutable() {
            // given
            String originalValue = "ORIGINAL-CODE";
            ExternalOptionCode code = ExternalOptionCode.of(originalValue);

            // when
            String retrievedValue = code.value();

            // then
            assertThat(retrievedValue).isEqualTo(originalValue);
            assertThat(code.value()).isEqualTo(originalValue);
        }
    }
}
