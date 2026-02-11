package com.ryuqq.marketplace.domain.saleschannelcategory.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategorySortKey 테스트")
class SalesChannelCategorySortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(SalesChannelCategorySortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 메서드 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 필드명은 createdAt이다")
        void createdAtFieldName() {
            // then
            assertThat(SalesChannelCategorySortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("EXTERNAL_NAME의 필드명은 externalCategoryName이다")
        void externalNameFieldName() {
            // then
            assertThat(SalesChannelCategorySortKey.EXTERNAL_NAME.fieldName())
                    .isEqualTo("externalCategoryName");
        }

        @Test
        @DisplayName("SORT_ORDER의 필드명은 sortOrder이다")
        void sortOrderFieldName() {
            // then
            assertThat(SalesChannelCategorySortKey.SORT_ORDER.fieldName()).isEqualTo("sortOrder");
        }
    }

    @Nested
    @DisplayName("defaultKey() 메서드 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 SORT_ORDER이다")
        void defaultKeyIsSortOrder() {
            // when
            SalesChannelCategorySortKey defaultKey = SalesChannelCategorySortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(SalesChannelCategorySortKey.SORT_ORDER);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SalesChannelCategorySortKey.values())
                    .containsExactly(
                            SalesChannelCategorySortKey.CREATED_AT,
                            SalesChannelCategorySortKey.EXTERNAL_NAME,
                            SalesChannelCategorySortKey.SORT_ORDER);
        }
    }
}
