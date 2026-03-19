package com.ryuqq.marketplace.domain.cancel.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelSortKey 단위 테스트")
class CancelSortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("REQUESTED_AT의 fieldName은 requestedAt이다")
        void requestedAtFieldName() {
            assertThat(CancelSortKey.REQUESTED_AT.fieldName()).isEqualTo("requestedAt");
        }

        @Test
        @DisplayName("COMPLETED_AT의 fieldName은 completedAt이다")
        void completedAtFieldName() {
            assertThat(CancelSortKey.COMPLETED_AT.fieldName()).isEqualTo("completedAt");
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(CancelSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(CancelSortKey.defaultKey()).isEqualTo(CancelSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("CancelSortKey는 3가지 값이다")
        void sortKeyValues() {
            assertThat(CancelSortKey.values())
                    .containsExactlyInAnyOrder(
                            CancelSortKey.REQUESTED_AT,
                            CancelSortKey.COMPLETED_AT,
                            CancelSortKey.CREATED_AT);
        }
    }
}
