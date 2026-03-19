package com.ryuqq.marketplace.domain.outboundproduct.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsProductSortKey enum 단위 테스트")
class OmsProductSortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(OmsProductSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("UPDATED_AT의 fieldName은 updatedAt이다")
        void updatedAtFieldName() {
            assertThat(OmsProductSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }

        @Test
        @DisplayName("PRODUCT_GROUP_NAME의 fieldName은 productGroupName이다")
        void productGroupNameFieldName() {
            assertThat(OmsProductSortKey.PRODUCT_GROUP_NAME.fieldName())
                    .isEqualTo("productGroupName");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(OmsProductSortKey.defaultKey()).isEqualTo(OmsProductSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("OmsProductSortKey는 3가지 값을 가진다")
        void hasCorrectNumberOfValues() {
            assertThat(OmsProductSortKey.values()).hasSize(3);
        }

        @Test
        @DisplayName("모든 정렬 키는 fieldName을 가진다")
        void allSortKeysHaveFieldName() {
            for (OmsProductSortKey sortKey : OmsProductSortKey.values()) {
                assertThat(sortKey.fieldName()).isNotNull().isNotBlank();
            }
        }
    }
}
