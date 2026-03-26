package com.ryuqq.marketplace.domain.exchange.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeDateField 단위 테스트")
class ExchangeDateFieldTest {

    @Nested
    @DisplayName("fieldName() - 필드명 반환")
    class FieldNameTest {

        @Test
        @DisplayName("REQUESTED의 fieldName은 requestedAt이다")
        void requestedFieldName() {
            assertThat(ExchangeDateField.REQUESTED.fieldName()).isEqualTo("requestedAt");
        }

        @Test
        @DisplayName("COMPLETED의 fieldName은 completedAt이다")
        void completedFieldName() {
            assertThat(ExchangeDateField.COMPLETED.fieldName()).isEqualTo("completedAt");
        }
    }

    @Nested
    @DisplayName("defaultField() - 기본 날짜 필드")
    class DefaultFieldTest {

        @Test
        @DisplayName("기본 날짜 필드는 REQUESTED이다")
        void defaultFieldIsRequested() {
            assertThat(ExchangeDateField.defaultField()).isEqualTo(ExchangeDateField.REQUESTED);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("2개의 날짜 필드가 존재한다")
        void twoDateFieldsExist() {
            assertThat(ExchangeDateField.values()).hasSize(2);
        }

        @Test
        @DisplayName("모든 날짜 필드 값이 존재한다")
        void allValuesExist() {
            assertThat(ExchangeDateField.values())
                    .containsExactly(ExchangeDateField.REQUESTED, ExchangeDateField.COMPLETED);
        }
    }
}
