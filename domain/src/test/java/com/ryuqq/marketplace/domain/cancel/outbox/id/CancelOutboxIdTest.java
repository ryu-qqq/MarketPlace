package com.ryuqq.marketplace.domain.cancel.outbox.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelOutboxId 단위 테스트")
class CancelOutboxIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            CancelOutboxId id = CancelOutboxId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> CancelOutboxId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew는 null 값을 가진 ID를 생성한다")
        void forNewCreatesIdWithNullValue() {
            CancelOutboxId id = CancelOutboxId.forNew();

            assertThat(id.value()).isNull();
        }

        @Test
        @DisplayName("forNew로 생성한 ID는 isNew()가 true이다")
        void forNewIdIsNew() {
            CancelOutboxId id = CancelOutboxId.forNew();

            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() 테스트")
    class IsNewTest {

        @Test
        @DisplayName("value가 null이면 isNew()가 true이다")
        void isNewWhenValueIsNull() {
            CancelOutboxId id = CancelOutboxId.forNew();

            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("value가 존재하면 isNew()가 false이다")
        void isNotNewWhenValueExists() {
            CancelOutboxId id = CancelOutboxId.of(100L);

            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 CancelOutboxId는 같다")
        void sameValuesAreEqual() {
            CancelOutboxId id1 = CancelOutboxId.of(1L);
            CancelOutboxId id2 = CancelOutboxId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 CancelOutboxId는 같지 않다")
        void differentValuesAreNotEqual() {
            CancelOutboxId id1 = CancelOutboxId.of(1L);
            CancelOutboxId id2 = CancelOutboxId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
