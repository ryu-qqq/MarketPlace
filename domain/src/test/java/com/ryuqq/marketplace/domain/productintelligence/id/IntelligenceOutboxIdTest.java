package com.ryuqq.marketplace.domain.productintelligence.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("IntelligenceOutboxId 단위 테스트")
class IntelligenceOutboxIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            IntelligenceOutboxId id = IntelligenceOutboxId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> IntelligenceOutboxId.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew는 value가 null인 ID를 생성한다")
        void forNewCreatesIdWithNullValue() {
            IntelligenceOutboxId id = IntelligenceOutboxId.forNew();

            assertThat(id.value()).isNull();
        }

        @Test
        @DisplayName("forNew로 생성된 ID는 isNew가 true이다")
        void forNewIsNewReturnsTrue() {
            IntelligenceOutboxId id = IntelligenceOutboxId.forNew();

            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() - 신규 여부 확인")
    class IsNewTest {

        @Test
        @DisplayName("value가 null이면 isNew는 true이다")
        void isNewWithNullValue() {
            IntelligenceOutboxId id = IntelligenceOutboxId.forNew();

            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("value가 존재하면 isNew는 false이다")
        void isNewWithExistingValue() {
            IntelligenceOutboxId id = IntelligenceOutboxId.of(1L);

            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 IntelligenceOutboxId는 같다")
        void sameValuesAreEqual() {
            IntelligenceOutboxId id1 = IntelligenceOutboxId.of(1L);
            IntelligenceOutboxId id2 = IntelligenceOutboxId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 IntelligenceOutboxId는 같지 않다")
        void differentValuesAreNotEqual() {
            IntelligenceOutboxId id1 = IntelligenceOutboxId.of(1L);
            IntelligenceOutboxId id2 = IntelligenceOutboxId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
