package com.ryuqq.marketplace.domain.refund.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.DateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundDateField 단위 테스트")
class RefundDateFieldTest {

    @Nested
    @DisplayName("fieldName() 테스트")
    class FieldNameTest {

        @Test
        @DisplayName("REQUESTED의 필드명은 requestedAt이다")
        void requestedHasCorrectFieldName() {
            assertThat(RefundDateField.REQUESTED.fieldName()).isEqualTo("requestedAt");
        }

        @Test
        @DisplayName("COMPLETED의 필드명은 completedAt이다")
        void completedHasCorrectFieldName() {
            assertThat(RefundDateField.COMPLETED.fieldName()).isEqualTo("completedAt");
        }
    }

    @Nested
    @DisplayName("defaultField() 테스트")
    class DefaultFieldTest {

        @Test
        @DisplayName("기본 날짜 필드는 REQUESTED이다")
        void defaultFieldIsRequested() {
            assertThat(RefundDateField.defaultField()).isEqualTo(RefundDateField.REQUESTED);
        }
    }

    @Nested
    @DisplayName("DateField 인터페이스 구현 테스트")
    class InterfaceTest {

        @Test
        @DisplayName("DateField 인터페이스를 구현한다")
        void implementsDateField() {
            assertThat(RefundDateField.REQUESTED).isInstanceOf(DateField.class);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("2개의 날짜 필드가 정의되어 있다")
        void hasTwoDateFields() {
            assertThat(RefundDateField.values()).hasSize(2);
        }

        @Test
        @DisplayName("모든 날짜 필드가 존재한다")
        void allDateFieldsExist() {
            assertThat(RefundDateField.values())
                    .containsExactly(RefundDateField.REQUESTED, RefundDateField.COMPLETED);
        }

        @Test
        @DisplayName("모든 날짜 필드는 비어있지 않은 필드명을 가진다")
        void allDateFieldsHaveNonBlankFieldName() {
            for (RefundDateField field : RefundDateField.values()) {
                assertThat(field.fieldName()).isNotBlank();
            }
        }
    }
}
