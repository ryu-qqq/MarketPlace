package com.ryuqq.marketplace.domain.commoncodetype.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeSortKey 테스트")
class CommonCodeTypeSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(CommonCodeTypeSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 'createdAt'이다")
        void createdAtFieldName() {
            assertThat(CommonCodeTypeSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("DISPLAY_ORDER의 fieldName은 'displayOrder'이다")
        void displayOrderFieldName() {
            assertThat(CommonCodeTypeSortKey.DISPLAY_ORDER.fieldName()).isEqualTo("displayOrder");
        }

        @Test
        @DisplayName("CODE의 fieldName은 'code'이다")
        void codeFieldName() {
            assertThat(CommonCodeTypeSortKey.CODE.fieldName()).isEqualTo("code");
        }
    }

    @Nested
    @DisplayName("defaultKey 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyReturnsCreatedAt() {
            // when
            CommonCodeTypeSortKey defaultKey = CommonCodeTypeSortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(CommonCodeTypeSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(CommonCodeTypeSortKey.values())
                    .containsExactly(
                            CommonCodeTypeSortKey.CREATED_AT,
                            CommonCodeTypeSortKey.DISPLAY_ORDER,
                            CommonCodeTypeSortKey.CODE);
        }

        @Test
        @DisplayName("valueOf로 enum 값을 조회한다")
        void valueOfReturnsCorrectEnum() {
            // then
            assertThat(CommonCodeTypeSortKey.valueOf("CREATED_AT"))
                    .isEqualTo(CommonCodeTypeSortKey.CREATED_AT);
            assertThat(CommonCodeTypeSortKey.valueOf("DISPLAY_ORDER"))
                    .isEqualTo(CommonCodeTypeSortKey.DISPLAY_ORDER);
            assertThat(CommonCodeTypeSortKey.valueOf("CODE")).isEqualTo(CommonCodeTypeSortKey.CODE);
        }
    }
}
