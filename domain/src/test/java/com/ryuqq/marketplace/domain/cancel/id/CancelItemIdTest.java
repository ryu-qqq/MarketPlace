package com.ryuqq.marketplace.domain.cancel.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelItemId 테스트")
class CancelItemIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ID를 생성한다")
        void createWithValidValue() {
            // when
            CancelItemId id = CancelItemId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> CancelItemId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 ID는 value가 null이고 isNew가 true이다")
        void createNewId() {
            // when
            CancelItemId id = CancelItemId.forNew();

            // then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() - 신규 여부 확인")
    class IsNewTest {

        @Test
        @DisplayName("value가 있으면 isNew가 false이다")
        void existingIdIsNotNew() {
            // when
            CancelItemId id = CancelItemId.of(100L);

            // then
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("forNew()로 생성한 ID는 isNew가 true이다")
        void newIdIsNew() {
            // when
            CancelItemId id = CancelItemId.forNew();

            // then
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            CancelItemId id1 = CancelItemId.of(1L);
            CancelItemId id2 = CancelItemId.of(1L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            CancelItemId id1 = CancelItemId.of(1L);
            CancelItemId id2 = CancelItemId.of(2L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
