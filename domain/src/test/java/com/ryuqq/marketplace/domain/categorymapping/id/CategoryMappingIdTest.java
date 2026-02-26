package com.ryuqq.marketplace.domain.categorymapping.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryMappingId Value Object 테스트")
class CategoryMappingIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 CategoryMappingId를 생성한다")
        void createWithOf() {
            CategoryMappingId id = CategoryMappingId.of(123L);
            assertThat(id.value()).isEqualTo(123L);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외를 발생시킨다")
        void ofWithNullThrowsException() {
            assertThatThrownBy(() -> CategoryMappingId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("forNew()로 새로운 매핑용 ID를 생성한다")
        void createWithForNew() {
            CategoryMappingId id = CategoryMappingId.forNew();
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
            assertThat(CategoryMappingId.forNew().isNew()).isTrue();
        }

        @Test
        @DisplayName("isNew()는 value가 있으면 false를 반환한다")
        void isNewReturnsFalseWhenValueExists() {
            assertThat(CategoryMappingId.of(1L).isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 CategoryMappingId는 동등하다")
        void sameValueEquals() {
            CategoryMappingId id1 = CategoryMappingId.of(100L);
            CategoryMappingId id2 = CategoryMappingId.of(100L);
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 CategoryMappingId는 동등하지 않다")
        void differentValueNotEquals() {
            assertThat(CategoryMappingId.of(100L)).isNotEqualTo(CategoryMappingId.of(200L));
        }
    }
}
