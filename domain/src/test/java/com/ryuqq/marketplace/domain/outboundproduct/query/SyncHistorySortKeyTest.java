package com.ryuqq.marketplace.domain.outboundproduct.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SyncHistorySortKey enum 단위 테스트")
class SyncHistorySortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(SyncHistorySortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(SyncHistorySortKey.defaultKey()).isEqualTo(SyncHistorySortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("SyncHistorySortKey는 1가지 값을 가진다")
        void hasCorrectNumberOfValues() {
            assertThat(SyncHistorySortKey.values()).hasSize(1);
        }
    }
}
