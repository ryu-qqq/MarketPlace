package com.ryuqq.marketplace.domain.commoncode.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeSortKey 테스트")
class CommonCodeSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(CommonCodeSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 'createdAt'이다")
        void createdAtFieldName() {
            assertThat(CommonCodeSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("DISPLAY_ORDER의 fieldName은 'displayOrder'이다")
        void displayOrderFieldName() {
            assertThat(CommonCodeSortKey.DISPLAY_ORDER.fieldName()).isEqualTo("displayOrder");
        }

        @Test
        @DisplayName("CODE의 fieldName은 'code'이다")
        void codeFieldName() {
            assertThat(CommonCodeSortKey.CODE.fieldName()).isEqualTo("code");
        }
    }

    @Nested
    @DisplayName("defaultKey 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyReturnsCreatedAt() {
            // when
            CommonCodeSortKey defaultKey = CommonCodeSortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(CommonCodeSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(CommonCodeSortKey.values())
                    .containsExactly(
                            CommonCodeSortKey.CREATED_AT,
                            CommonCodeSortKey.DISPLAY_ORDER,
                            CommonCodeSortKey.CODE);
        }

        @Test
        @DisplayName("valueOf로 enum 값을 조회한다")
        void valueOfReturnsCorrectEnum() {
            // then
            assertThat(CommonCodeSortKey.valueOf("CREATED_AT"))
                    .isEqualTo(CommonCodeSortKey.CREATED_AT);
            assertThat(CommonCodeSortKey.valueOf("DISPLAY_ORDER"))
                    .isEqualTo(CommonCodeSortKey.DISPLAY_ORDER);
            assertThat(CommonCodeSortKey.valueOf("CODE")).isEqualTo(CommonCodeSortKey.CODE);
        }
    }
}
