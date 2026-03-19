package com.ryuqq.marketplace.domain.settlement.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementDateField 단위 테스트")
class SettlementDateFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("EXPECTED_SETTLEMENT의 fieldName은 expectedSettlementDay이다")
        void expectedSettlementFieldName() {
            assertThat(SettlementDateField.EXPECTED_SETTLEMENT.fieldName())
                    .isEqualTo("expectedSettlementDay");
        }

        @Test
        @DisplayName("SETTLEMENT의 fieldName은 settlementDay이다")
        void settlementFieldName() {
            assertThat(SettlementDateField.SETTLEMENT.fieldName()).isEqualTo("settlementDay");
        }

        @Test
        @DisplayName("ORDERED의 fieldName은 orderedAt이다")
        void orderedFieldName() {
            assertThat(SettlementDateField.ORDERED.fieldName()).isEqualTo("orderedAt");
        }
    }

    @Nested
    @DisplayName("defaultField() 테스트")
    class DefaultFieldTest {

        @Test
        @DisplayName("기본 날짜 필드는 EXPECTED_SETTLEMENT이다")
        void defaultFieldIsExpectedSettlement() {
            assertThat(SettlementDateField.defaultField())
                    .isEqualTo(SettlementDateField.EXPECTED_SETTLEMENT);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("SettlementDateField는 3가지 값이다")
        void dateFieldValues() {
            SettlementDateField[] values = SettlementDateField.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementDateField.EXPECTED_SETTLEMENT,
                            SettlementDateField.SETTLEMENT,
                            SettlementDateField.ORDERED);
        }
    }
}
