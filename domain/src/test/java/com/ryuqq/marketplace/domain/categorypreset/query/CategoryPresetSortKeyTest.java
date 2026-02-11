package com.ryuqq.marketplace.domain.categorypreset.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetSortKey 테스트")
class CategoryPresetSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            assertThat(CategoryPresetSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 필드명은 createdAt이다")
        void createdAtFieldName() {
            assertThat(CategoryPresetSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(CategoryPresetSortKey.defaultKey())
                    .isEqualTo(CategoryPresetSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            assertThat(CategoryPresetSortKey.values())
                    .containsExactly(CategoryPresetSortKey.CREATED_AT);
        }
    }
}
