package com.ryuqq.marketplace.domain.seller.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerSortKey 테스트")
class SellerSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(SellerSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 필드명은 createdAt이다")
        void createdAtFieldName() {
            // then
            assertThat(SellerSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("SELLER_NAME의 필드명은 sellerName이다")
        void sellerNameFieldName() {
            // then
            assertThat(SellerSortKey.SELLER_NAME.fieldName()).isEqualTo("sellerName");
        }

        @Test
        @DisplayName("DISPLAY_NAME의 필드명은 displayName이다")
        void displayNameFieldName() {
            // then
            assertThat(SellerSortKey.DISPLAY_NAME.fieldName()).isEqualTo("displayName");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            // then
            assertThat(SellerSortKey.defaultKey()).isEqualTo(SellerSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SellerSortKey.values())
                    .containsExactly(
                            SellerSortKey.CREATED_AT,
                            SellerSortKey.SELLER_NAME,
                            SellerSortKey.DISPLAY_NAME);
        }
    }
}
