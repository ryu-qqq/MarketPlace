package com.ryuqq.marketplace.domain.shippingpolicy.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicySortKey 테스트")
class ShippingPolicySortKeyTest {

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTest {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void implementsSortKey() {
            // then
            assertThat(ShippingPolicySortKey.CREATED_AT).isInstanceOf(SortKey.class);
        }

        @Test
        @DisplayName("CREATED_AT의 fieldName은 'createdAt'이다")
        void createdAtFieldName() {
            assertThat(ShippingPolicySortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("POLICY_NAME의 fieldName은 'policyName'이다")
        void policyNameFieldName() {
            assertThat(ShippingPolicySortKey.POLICY_NAME.fieldName()).isEqualTo("policyName");
        }

        @Test
        @DisplayName("BASE_FEE의 fieldName은 'baseFee'이다")
        void baseFeeFieldName() {
            assertThat(ShippingPolicySortKey.BASE_FEE.fieldName()).isEqualTo("baseFee");
        }
    }

    @Nested
    @DisplayName("defaultKey 테스트")
    class DefaultKeyTest {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyReturnsCreatedAt() {
            // when
            ShippingPolicySortKey defaultKey = ShippingPolicySortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(ShippingPolicySortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 정렬 키 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ShippingPolicySortKey.values())
                    .containsExactly(
                            ShippingPolicySortKey.CREATED_AT,
                            ShippingPolicySortKey.POLICY_NAME,
                            ShippingPolicySortKey.BASE_FEE);
        }

        @Test
        @DisplayName("valueOf로 enum 값을 조회한다")
        void valueOfReturnsCorrectEnum() {
            // then
            assertThat(ShippingPolicySortKey.valueOf("CREATED_AT"))
                    .isEqualTo(ShippingPolicySortKey.CREATED_AT);
            assertThat(ShippingPolicySortKey.valueOf("POLICY_NAME"))
                    .isEqualTo(ShippingPolicySortKey.POLICY_NAME);
            assertThat(ShippingPolicySortKey.valueOf("BASE_FEE"))
                    .isEqualTo(ShippingPolicySortKey.BASE_FEE);
        }
    }
}
