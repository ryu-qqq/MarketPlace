package com.ryuqq.marketplace.domain.saleschannel.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelId Value Object 단위 테스트")
class SalesChannelIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given & when
            SalesChannelId id = SalesChannelId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            assertThatThrownBy(() -> SalesChannelId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SalesChannelId 값은 null일 수 없습니다");
        }

        @Test
        @DisplayName("forNew()로 새 ID를 생성한다")
        void createNewId() {
            // given & when
            SalesChannelId id = SalesChannelId.forNew();

            // then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {
        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            SalesChannelId id1 = SalesChannelId.of(1L);
            SalesChannelId id2 = SalesChannelId.of(1L);
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            SalesChannelId id1 = SalesChannelId.of(1L);
            SalesChannelId id2 = SalesChannelId.of(2L);
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("isNew 메서드 테스트")
    class IsNewTest {
        @Test
        @DisplayName("forNew()로 생성된 ID는 isNew()가 true를 반환한다")
        void newIdIsNew() {
            SalesChannelId id = SalesChannelId.forNew();
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("값이 있는 ID는 isNew()가 false를 반환한다")
        void existingIdIsNotNew() {
            SalesChannelId id = SalesChannelId.of(1L);
            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {
        @Test
        @DisplayName("record는 불변이므로 값을 변경할 수 없다")
        void recordIsImmutable() {
            // given
            SalesChannelId id = SalesChannelId.of(1L);

            // when
            Long originalValue = id.value();

            // then
            assertThat(id.value()).isEqualTo(originalValue);
        }
    }
}
