package com.ryuqq.marketplace.domain.canonicaloption.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupSortKey 단위 테스트")
class CanonicalOptionGroupSortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(CanonicalOptionGroupSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 필드명은 createdAt이다")
        void createdAtFieldName() {
            // then
            assertThat(CanonicalOptionGroupSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("CODE의 필드명은 code이다")
        void codeFieldName() {
            // then
            assertThat(CanonicalOptionGroupSortKey.CODE.fieldName()).isEqualTo("code");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            // then
            assertThat(CanonicalOptionGroupSortKey.defaultKey())
                    .isEqualTo(CanonicalOptionGroupSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(CanonicalOptionGroupSortKey.values())
                    .containsExactly(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            CanonicalOptionGroupSortKey.CODE);
        }

        @Test
        @DisplayName("2개의 정렬 키가 존재한다")
        void hasTwoSortKeys() {
            // then
            assertThat(CanonicalOptionGroupSortKey.values()).hasSize(2);
        }
    }
}
