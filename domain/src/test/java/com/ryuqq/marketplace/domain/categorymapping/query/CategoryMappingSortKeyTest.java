package com.ryuqq.marketplace.domain.categorymapping.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryMappingSortKey 테스트")
class CategoryMappingSortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 fieldName을 반환한다")
        void createdAtFieldName() {
            assertThat(CategoryMappingSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(CategoryMappingSortKey.defaultKey())
                    .isEqualTo(CategoryMappingSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            assertThat(CategoryMappingSortKey.values())
                    .containsExactly(CategoryMappingSortKey.CREATED_AT);
        }
    }
}
