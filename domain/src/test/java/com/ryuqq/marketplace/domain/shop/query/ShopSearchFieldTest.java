package com.ryuqq.marketplace.domain.shop.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopSearchField 단위 테스트")
class ShopSearchFieldTest {

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class SearchFieldInterfaceTest {

        @Test
        @DisplayName("SearchField 인터페이스를 구현한다")
        void implementsSearchField() {
            // then
            assertThat(ShopSearchField.SHOP_NAME).isInstanceOf(SearchField.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("SHOP_NAME의 fieldName은 'shopName'이다")
        void shopNameFieldName() {
            // then
            assertThat(ShopSearchField.SHOP_NAME.fieldName()).isEqualTo("shopName");
        }

        @Test
        @DisplayName("ACCOUNT_ID의 fieldName은 'accountId'이다")
        void accountIdFieldName() {
            // then
            assertThat(ShopSearchField.ACCOUNT_ID.fieldName()).isEqualTo("accountId");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("'shopName' 문자열로 SHOP_NAME을 반환한다")
        void createShopNameFromString() {
            // when
            ShopSearchField field = ShopSearchField.fromString("shopName");

            // then
            assertThat(field).isEqualTo(ShopSearchField.SHOP_NAME);
        }

        @Test
        @DisplayName("'SHOP_NAME' 문자열로 SHOP_NAME을 반환한다")
        void createShopNameFromEnumName() {
            // when
            ShopSearchField field = ShopSearchField.fromString("SHOP_NAME");

            // then
            assertThat(field).isEqualTo(ShopSearchField.SHOP_NAME);
        }

        @Test
        @DisplayName("대소문자 구분 없이 SHOP_NAME을 반환한다")
        void createShopNameCaseInsensitive() {
            // when
            ShopSearchField field1 = ShopSearchField.fromString("shopname");
            ShopSearchField field2 = ShopSearchField.fromString("ShOpNaMe");

            // then
            assertThat(field1).isEqualTo(ShopSearchField.SHOP_NAME);
            assertThat(field2).isEqualTo(ShopSearchField.SHOP_NAME);
        }

        @Test
        @DisplayName("'accountId' 문자열로 ACCOUNT_ID를 반환한다")
        void createAccountIdFromString() {
            // when
            ShopSearchField field = ShopSearchField.fromString("accountId");

            // then
            assertThat(field).isEqualTo(ShopSearchField.ACCOUNT_ID);
        }

        @Test
        @DisplayName("'ACCOUNT_ID' 문자열로 ACCOUNT_ID를 반환한다")
        void createAccountIdFromEnumName() {
            // when
            ShopSearchField field = ShopSearchField.fromString("ACCOUNT_ID");

            // then
            assertThat(field).isEqualTo(ShopSearchField.ACCOUNT_ID);
        }

        @Test
        @DisplayName("null 문자열은 null을 반환한다")
        void createFromNullReturnsNull() {
            // when
            ShopSearchField field = ShopSearchField.fromString(null);

            // then
            assertThat(field).isNull();
        }

        @Test
        @DisplayName("빈 문자열은 null을 반환한다")
        void createFromBlankReturnsNull() {
            // when
            ShopSearchField field = ShopSearchField.fromString("   ");

            // then
            assertThat(field).isNull();
        }

        @Test
        @DisplayName("잘못된 문자열은 null을 반환한다")
        void createFromInvalidStringReturnsNull() {
            // when
            ShopSearchField field = ShopSearchField.fromString("INVALID_FIELD");

            // then
            assertThat(field).isNull();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 ShopSearchField 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ShopSearchField.values())
                    .containsExactly(ShopSearchField.SHOP_NAME, ShopSearchField.ACCOUNT_ID);
        }

        @Test
        @DisplayName("valueOf()로 SHOP_NAME을 가져온다")
        void valueOfShopName() {
            // when
            ShopSearchField field = ShopSearchField.valueOf("SHOP_NAME");

            // then
            assertThat(field).isEqualTo(ShopSearchField.SHOP_NAME);
        }

        @Test
        @DisplayName("valueOf()로 ACCOUNT_ID를 가져온다")
        void valueOfAccountId() {
            // when
            ShopSearchField field = ShopSearchField.valueOf("ACCOUNT_ID");

            // then
            assertThat(field).isEqualTo(ShopSearchField.ACCOUNT_ID);
        }
    }

    @Nested
    @DisplayName("필드명 매핑 테스트")
    class FieldMappingTest {

        @Test
        @DisplayName("모든 필드는 고유한 fieldName을 가진다")
        void allFieldsHaveUniqueFieldName() {
            // then
            ShopSearchField[] fields = ShopSearchField.values();
            for (int i = 0; i < fields.length; i++) {
                for (int j = i + 1; j < fields.length; j++) {
                    assertThat(fields[i].fieldName()).isNotEqualTo(fields[j].fieldName());
                }
            }
        }

        @Test
        @DisplayName("모든 필드의 fieldName은 camelCase 형식이다")
        void allFieldNamesAreCamelCase() {
            // then
            for (ShopSearchField field : ShopSearchField.values()) {
                String fieldName = field.fieldName();
                assertThat(fieldName).matches("[a-z][a-zA-Z0-9]*"); // camelCase 패턴
            }
        }
    }
}
