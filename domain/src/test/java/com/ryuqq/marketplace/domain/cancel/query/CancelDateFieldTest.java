package com.ryuqq.marketplace.domain.cancel.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelDateField 단위 테스트")
class CancelDateFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("REQUESTED의 fieldName은 requestedAt이다")
        void requestedFieldName() {
            assertThat(CancelDateField.REQUESTED.fieldName()).isEqualTo("requestedAt");
        }

        @Test
        @DisplayName("COMPLETED의 fieldName은 completedAt이다")
        void completedFieldName() {
            assertThat(CancelDateField.COMPLETED.fieldName()).isEqualTo("completedAt");
        }
    }

    @Nested
    @DisplayName("defaultField() 테스트")
    class DefaultFieldTest {

        @Test
        @DisplayName("기본 날짜 필드는 REQUESTED이다")
        void defaultFieldIsRequested() {
            assertThat(CancelDateField.defaultField()).isEqualTo(CancelDateField.REQUESTED);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("CancelDateField는 2가지 값이다")
        void dateFieldValues() {
            assertThat(CancelDateField.values())
                    .containsExactlyInAnyOrder(
                            CancelDateField.REQUESTED, CancelDateField.COMPLETED);
        }
    }
}
