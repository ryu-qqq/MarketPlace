package com.ryuqq.marketplace.domain.refundpolicy.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicySortKey 테스트")
class RefundPolicySortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(RefundPolicySortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 'createdAt'이다")
        void createdAtFieldName() {
            assertThat(RefundPolicySortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("POLICY_NAME의 fieldName은 'policyName'이다")
        void policyNameFieldName() {
            assertThat(RefundPolicySortKey.POLICY_NAME.fieldName()).isEqualTo("policyName");
        }

        @Test
        @DisplayName("RETURN_PERIOD_DAYS의 fieldName은 'returnPeriodDays'이다")
        void returnPeriodDaysFieldName() {
            assertThat(RefundPolicySortKey.RETURN_PERIOD_DAYS.fieldName())
                    .isEqualTo("returnPeriodDays");
        }
    }

    @Nested
    @DisplayName("defaultKey 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyReturnsCreatedAt() {
            // when
            RefundPolicySortKey defaultKey = RefundPolicySortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(RefundPolicySortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(RefundPolicySortKey.values())
                    .containsExactly(
                            RefundPolicySortKey.CREATED_AT,
                            RefundPolicySortKey.POLICY_NAME,
                            RefundPolicySortKey.RETURN_PERIOD_DAYS);
        }

        @Test
        @DisplayName("valueOf로 enum 값을 조회한다")
        void valueOfReturnsCorrectEnum() {
            // then
            assertThat(RefundPolicySortKey.valueOf("CREATED_AT"))
                    .isEqualTo(RefundPolicySortKey.CREATED_AT);
            assertThat(RefundPolicySortKey.valueOf("POLICY_NAME"))
                    .isEqualTo(RefundPolicySortKey.POLICY_NAME);
            assertThat(RefundPolicySortKey.valueOf("RETURN_PERIOD_DAYS"))
                    .isEqualTo(RefundPolicySortKey.RETURN_PERIOD_DAYS);
        }
    }
}
