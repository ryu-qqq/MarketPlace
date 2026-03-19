package com.ryuqq.marketplace.domain.settlement.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementSortKey 단위 테스트")
class SettlementSortKeyTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("EXPECTED_SETTLEMENT_DAY의 fieldName은 expectedSettlementDay이다")
        void expectedSettlementDayFieldName() {
            assertThat(SettlementSortKey.EXPECTED_SETTLEMENT_DAY.fieldName())
                    .isEqualTo("expectedSettlementDay");
        }

        @Test
        @DisplayName("SETTLEMENT_DAY의 fieldName은 settlementDay이다")
        void settlementDayFieldName() {
            assertThat(SettlementSortKey.SETTLEMENT_DAY.fieldName()).isEqualTo("settlementDay");
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtFieldName() {
            assertThat(SettlementSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            assertThat(SettlementSortKey.defaultKey()).isEqualTo(SettlementSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("SettlementSortKey는 3가지 값이다")
        void sortKeyValues() {
            SettlementSortKey[] values = SettlementSortKey.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementSortKey.EXPECTED_SETTLEMENT_DAY,
                            SettlementSortKey.SETTLEMENT_DAY,
                            SettlementSortKey.CREATED_AT);
        }
    }
}
