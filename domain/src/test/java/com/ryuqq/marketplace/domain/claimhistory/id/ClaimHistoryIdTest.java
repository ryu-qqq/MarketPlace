package com.ryuqq.marketplace.domain.claimhistory.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimHistoryId Value Object 테스트")
class ClaimHistoryIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 ClaimHistoryId를 생성한다")
        void createWithOf() {
            // given
            String value = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70";

            // when
            ClaimHistoryId id = ClaimHistoryId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("forNew()로 ClaimHistoryId를 생성한다")
        void createWithForNew() {
            // given
            String value = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70";

            // when
            ClaimHistoryId id = ClaimHistoryId.forNew(value);

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("generate()로 UUID 기반 ClaimHistoryId를 자동 생성한다")
        void createWithGenerate() {
            // when
            ClaimHistoryId id = ClaimHistoryId.generate();

            // then
            assertThat(id.value()).isNotNull();
            assertThat(id.value()).isNotBlank();
        }

        @Test
        @DisplayName("generate()는 호출할 때마다 다른 값을 생성한다")
        void generateProducesUniqueValues() {
            // when
            ClaimHistoryId id1 = ClaimHistoryId.generate();
            ClaimHistoryId id2 = ClaimHistoryId.generate();

            // then
            assertThat(id1.value()).isNotEqualTo(id2.value());
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외가 발생한다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> ClaimHistoryId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("of()에 빈 문자열을 전달하면 예외가 발생한다")
        void ofWithBlankThrowsException() {
            // when & then
            assertThatThrownBy(() -> ClaimHistoryId.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("of()에 공백만 있는 문자열을 전달하면 예외가 발생한다")
        void ofWithWhitespaceThrowsException() {
            // when & then
            assertThatThrownBy(() -> ClaimHistoryId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 ClaimHistoryId는 동등하다")
        void sameValueEquals() {
            // given
            String value = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70";
            ClaimHistoryId id1 = ClaimHistoryId.of(value);
            ClaimHistoryId id2 = ClaimHistoryId.of(value);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ClaimHistoryId는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            ClaimHistoryId id1 = ClaimHistoryId.of("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70");
            ClaimHistoryId id2 = ClaimHistoryId.of("01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f71");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ClaimHistoryId는 record이므로 불변이다")
        void claimHistoryIdIsImmutable() {
            // given
            String value = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f70";
            ClaimHistoryId id = ClaimHistoryId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
        }
    }
}
