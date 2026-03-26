package com.ryuqq.marketplace.domain.qna.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaId 단위 테스트")
class QnaIdTest {

    @Nested
    @DisplayName("of() - 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            // when
            QnaId id = QnaId.of(1L);

            // then
            assertThat(id).isNotNull();
            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> QnaId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("QnaId value must not be null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew()로 생성하면 value가 null이다")
        void forNewHasNullValue() {
            // when
            QnaId id = QnaId.forNew();

            // then
            assertThat(id.value()).isNull();
        }

        @Test
        @DisplayName("forNew()로 생성하면 isNew()가 true이다")
        void forNewIsNew() {
            // when
            QnaId id = QnaId.forNew();

            // then
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() - 신규 여부 확인")
    class IsNewTest {

        @Test
        @DisplayName("value가 null이면 isNew()는 true이다")
        void isNewWhenValueIsNull() {
            QnaId id = QnaId.forNew();
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("value가 존재하면 isNew()는 false이다")
        void isNotNewWhenValueExists() {
            QnaId id = QnaId.of(100L);
            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 value를 가진 ID는 동일하다")
        void sameValueAreEqual() {
            QnaId id1 = QnaId.of(1L);
            QnaId id2 = QnaId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 value를 가진 ID는 동일하지 않다")
        void differentValuesAreNotEqual() {
            QnaId id1 = QnaId.of(1L);
            QnaId id2 = QnaId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
