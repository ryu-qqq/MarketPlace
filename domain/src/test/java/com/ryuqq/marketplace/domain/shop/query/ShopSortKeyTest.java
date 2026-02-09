package com.ryuqq.marketplace.domain.shop.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopSortKey 단위 테스트")
class ShopSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(ShopSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 'createdAt'이다")
        void createdAtFieldName() {
            // then
            assertThat(ShopSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("UPDATED_AT의 fieldName은 'updatedAt'이다")
        void updatedAtFieldName() {
            // then
            assertThat(ShopSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }

        @Test
        @DisplayName("SHOP_NAME의 fieldName은 'shopName'이다")
        void shopNameFieldName() {
            // then
            assertThat(ShopSortKey.SHOP_NAME.fieldName()).isEqualTo("shopName");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            // when
            ShopSortKey defaultKey = ShopSortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(ShopSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 ShopSortKey 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ShopSortKey.values())
                    .containsExactly(
                            ShopSortKey.CREATED_AT, ShopSortKey.UPDATED_AT, ShopSortKey.SHOP_NAME);
        }

        @Test
        @DisplayName("valueOf()로 CREATED_AT을 가져온다")
        void valueOfCreatedAt() {
            // when
            ShopSortKey sortKey = ShopSortKey.valueOf("CREATED_AT");

            // then
            assertThat(sortKey).isEqualTo(ShopSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("valueOf()로 UPDATED_AT을 가져온다")
        void valueOfUpdatedAt() {
            // when
            ShopSortKey sortKey = ShopSortKey.valueOf("UPDATED_AT");

            // then
            assertThat(sortKey).isEqualTo(ShopSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("valueOf()로 SHOP_NAME을 가져온다")
        void valueOfShopName() {
            // when
            ShopSortKey sortKey = ShopSortKey.valueOf("SHOP_NAME");

            // then
            assertThat(sortKey).isEqualTo(ShopSortKey.SHOP_NAME);
        }
    }

    @Nested
    @DisplayName("필드명 매핑 테스트")
    class FieldMappingTest {

        @Test
        @DisplayName("모든 정렬 키는 고유한 fieldName을 가진다")
        void allSortKeysHaveUniqueFieldName() {
            // then
            ShopSortKey[] sortKeys = ShopSortKey.values();
            for (int i = 0; i < sortKeys.length; i++) {
                for (int j = i + 1; j < sortKeys.length; j++) {
                    assertThat(sortKeys[i].fieldName()).isNotEqualTo(sortKeys[j].fieldName());
                }
            }
        }

        @Test
        @DisplayName("모든 정렬 키의 fieldName은 camelCase 형식이다")
        void allFieldNamesAreCamelCase() {
            // then
            for (ShopSortKey sortKey : ShopSortKey.values()) {
                String fieldName = sortKey.fieldName();
                assertThat(fieldName).matches("[a-z][a-zA-Z0-9]*"); // camelCase 패턴
            }
        }

        @Test
        @DisplayName("모든 정렬 키의 fieldName은 null이나 빈 문자열이 아니다")
        void allFieldNamesAreNotBlank() {
            // then
            for (ShopSortKey sortKey : ShopSortKey.values()) {
                assertThat(sortKey.fieldName()).isNotNull().isNotBlank();
            }
        }
    }

    @Nested
    @DisplayName("정렬 키 사용성 테스트")
    class UsabilityTest {

        @Test
        @DisplayName("시간 기반 정렬 키들이 존재한다")
        void timeBasedSortKeysExist() {
            // then
            assertThat(ShopSortKey.values())
                    .contains(ShopSortKey.CREATED_AT, ShopSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("이름 기반 정렬 키가 존재한다")
        void nameBasedSortKeyExists() {
            // then
            assertThat(ShopSortKey.values()).contains(ShopSortKey.SHOP_NAME);
        }
    }
}
