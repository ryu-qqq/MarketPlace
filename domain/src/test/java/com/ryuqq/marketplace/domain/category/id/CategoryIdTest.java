package com.ryuqq.marketplace.domain.category.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryId 단위 테스트")
class CategoryIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            CategoryId id = CategoryId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> CategoryId.of(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew는 null 값을 가진 ID를 생성한다")
        void createForNewWithNullValue() {
            CategoryId id = CategoryId.forNew();

            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() 테스트")
    class IsNewTest {

        @Test
        @DisplayName("value가 null이면 isNew가 true다")
        void nullValueIsNew() {
            CategoryId id = CategoryId.forNew();

            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("value가 있으면 isNew가 false다")
        void nonNullValueIsNotNew() {
            CategoryId id = CategoryId.of(100L);

            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 CategoryId는 같다")
        void sameValuesAreEqual() {
            CategoryId id1 = CategoryId.of(1L);
            CategoryId id2 = CategoryId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 CategoryId는 같지 않다")
        void differentValuesAreNotEqual() {
            CategoryId id1 = CategoryId.of(1L);
            CategoryId id2 = CategoryId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
