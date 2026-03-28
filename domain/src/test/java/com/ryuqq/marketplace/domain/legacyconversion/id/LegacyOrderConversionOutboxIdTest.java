package com.ryuqq.marketplace.domain.legacyconversion.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyOrderConversionOutboxId Value Object 테스트")
class LegacyOrderConversionOutboxIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 ID를 생성한다")
        void createWithOf() {
            // when
            LegacyOrderConversionOutboxId id = LegacyOrderConversionOutboxId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외가 발생한다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> LegacyOrderConversionOutboxId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("forNew()로 신규 ID를 생성한다")
        void createWithForNew() {
            // when
            LegacyOrderConversionOutboxId id = LegacyOrderConversionOutboxId.forNew();

            // then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StateCheckTest {

        @Test
        @DisplayName("isNew()는 value가 null이면 true를 반환한다")
        void isNewReturnsTrueWhenValueIsNull() {
            assertThat(LegacyOrderConversionOutboxId.forNew().isNew()).isTrue();
        }

        @Test
        @DisplayName("isNew()는 value가 존재하면 false를 반환한다")
        void isNewReturnsFalseWhenValueExists() {
            assertThat(LegacyOrderConversionOutboxId.of(1L).isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 ID는 동등하다")
        void sameValueEquals() {
            // given
            LegacyOrderConversionOutboxId id1 = LegacyOrderConversionOutboxId.of(100L);
            LegacyOrderConversionOutboxId id2 = LegacyOrderConversionOutboxId.of(100L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ID는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            LegacyOrderConversionOutboxId id1 = LegacyOrderConversionOutboxId.of(100L);
            LegacyOrderConversionOutboxId id2 = LegacyOrderConversionOutboxId.of(200L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
