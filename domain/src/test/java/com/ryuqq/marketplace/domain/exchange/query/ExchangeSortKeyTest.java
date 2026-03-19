package com.ryuqq.marketplace.domain.exchange.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeSortKey 단위 테스트")
class ExchangeSortKeyTest {

    @Nested
    @DisplayName("fieldName() - 필드명 반환")
    class FieldNameTest {

        @Test
        @DisplayName("REQUESTED_AT의 fieldName은 requestedAt이다")
        void requestedAtFieldName() {
            assertThat(ExchangeSortKey.REQUESTED_AT.fieldName()).isEqualTo("requestedAt");
        }

        @Test
        @DisplayName("COMPLETED_AT의 fieldName은 completedAt이다")
        void completedAtFieldName() {
            assertThat(ExchangeSortKey.COMPLETED_AT.fieldName()).isEqualTo("completedAt");
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(ExchangeSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() - 기본 정렬 키")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(ExchangeSortKey.defaultKey()).isEqualTo(ExchangeSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("3개의 정렬 키가 존재한다")
        void threeSortKeysExist() {
            assertThat(ExchangeSortKey.values()).hasSize(3);
        }

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            assertThat(ExchangeSortKey.values())
                    .containsExactly(
                            ExchangeSortKey.REQUESTED_AT,
                            ExchangeSortKey.COMPLETED_AT,
                            ExchangeSortKey.CREATED_AT);
        }
    }
}
