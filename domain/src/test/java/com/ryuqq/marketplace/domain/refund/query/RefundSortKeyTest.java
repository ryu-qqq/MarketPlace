package com.ryuqq.marketplace.domain.refund.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundSortKey 단위 테스트")
class RefundSortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("REQUESTED_AT의 필드명은 requestedAt이다")
        void requestedAtHasCorrectFieldName() {
            assertThat(RefundSortKey.REQUESTED_AT.fieldName()).isEqualTo("requestedAt");
        }

        @Test
        @DisplayName("COMPLETED_AT의 필드명은 completedAt이다")
        void completedAtHasCorrectFieldName() {
            assertThat(RefundSortKey.COMPLETED_AT.fieldName()).isEqualTo("completedAt");
        }

        @Test
        @DisplayName("CREATED_AT의 필드명은 createdAt이다")
        void createdAtHasCorrectFieldName() {
            assertThat(RefundSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(RefundSortKey.defaultKey()).isEqualTo(RefundSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class InterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            assertThat(RefundSortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("3개의 정렬 키가 정의되어 있다")
        void hasThreeSortKeys() {
            assertThat(RefundSortKey.values()).hasSize(3);
        }

        @Test
        @DisplayName("모든 정렬 키가 존재한다")
        void allSortKeysExist() {
            assertThat(RefundSortKey.values())
                    .containsExactly(
                            RefundSortKey.REQUESTED_AT,
                            RefundSortKey.COMPLETED_AT,
                            RefundSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("모든 정렬 키는 비어있지 않은 필드명을 가진다")
        void allSortKeysHaveNonBlankFieldName() {
            for (RefundSortKey key : RefundSortKey.values()) {
                assertThat(key.fieldName()).isNotBlank();
            }
        }
    }
}
