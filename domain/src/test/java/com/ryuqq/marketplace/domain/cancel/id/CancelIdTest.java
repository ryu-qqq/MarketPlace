package com.ryuqq.marketplace.domain.cancel.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelId 테스트")
class CancelIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ID를 생성한다")
        void createWithValidValue() {
            // given
            String uuidValue = "01900000-0000-7000-8000-000000000001";

            // when
            CancelId id = CancelId.of(uuidValue);

            // then
            assertThat(id.value()).isEqualTo(uuidValue);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelId.of("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("공백 문자열이면 예외가 발생한다")
        void createWithWhitespace_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelId.of("  "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("외부에서 주입된 값으로 신규 ID를 생성한다")
        void createNewIdWithInjectedValue() {
            // given
            String uuidValue = "01900000-0000-7000-8000-000000000002";

            // when
            CancelId id = CancelId.forNew(uuidValue);

            // then
            assertThat(id.value()).isEqualTo(uuidValue);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            String value = "01900000-0000-7000-8000-000000000001";
            CancelId id1 = CancelId.of(value);
            CancelId id2 = CancelId.of(value);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            CancelId id1 = CancelId.of("01900000-0000-7000-8000-000000000001");
            CancelId id2 = CancelId.of("01900000-0000-7000-8000-000000000002");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
