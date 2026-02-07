package com.ryuqq.marketplace.domain.shippingpolicy.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingFeeType Enum 테스트")
class ShippingFeeTypeTest {

    @Nested
    @DisplayName("isFree() 테스트")
    class IsFreeTest {

        @Test
        @DisplayName("FREE 타입만 true를 반환한다")
        void onlyFreeReturnsTrue() {
            assertThat(ShippingFeeType.FREE.isFree()).isTrue();
            assertThat(ShippingFeeType.PAID.isFree()).isFalse();
            assertThat(ShippingFeeType.CONDITIONAL_FREE.isFree()).isFalse();
            assertThat(ShippingFeeType.QUANTITY_BASED.isFree()).isFalse();
        }
    }

    @Nested
    @DisplayName("isPaid() 테스트")
    class IsPaidTest {

        @Test
        @DisplayName("PAID 타입만 true를 반환한다")
        void onlyPaidReturnsTrue() {
            assertThat(ShippingFeeType.PAID.isPaid()).isTrue();
            assertThat(ShippingFeeType.FREE.isPaid()).isFalse();
            assertThat(ShippingFeeType.CONDITIONAL_FREE.isPaid()).isFalse();
            assertThat(ShippingFeeType.QUANTITY_BASED.isPaid()).isFalse();
        }
    }

    @Nested
    @DisplayName("isConditionalFree() 테스트")
    class IsConditionalFreeTest {

        @Test
        @DisplayName("CONDITIONAL_FREE 타입만 true를 반환한다")
        void onlyConditionalFreeReturnsTrue() {
            assertThat(ShippingFeeType.CONDITIONAL_FREE.isConditionalFree()).isTrue();
            assertThat(ShippingFeeType.FREE.isConditionalFree()).isFalse();
            assertThat(ShippingFeeType.PAID.isConditionalFree()).isFalse();
            assertThat(ShippingFeeType.QUANTITY_BASED.isConditionalFree()).isFalse();
        }
    }

    @Nested
    @DisplayName("isQuantityBased() 테스트")
    class IsQuantityBasedTest {

        @Test
        @DisplayName("QUANTITY_BASED 타입만 true를 반환한다")
        void onlyQuantityBasedReturnsTrue() {
            assertThat(ShippingFeeType.QUANTITY_BASED.isQuantityBased()).isTrue();
            assertThat(ShippingFeeType.FREE.isQuantityBased()).isFalse();
            assertThat(ShippingFeeType.PAID.isQuantityBased()).isFalse();
            assertThat(ShippingFeeType.CONDITIONAL_FREE.isQuantityBased()).isFalse();
        }
    }

    @Nested
    @DisplayName("displayName() 테스트")
    class DisplayNameTest {

        @Test
        @DisplayName("각 타입에 맞는 노출명을 반환한다")
        void returnsCorrectDisplayName() {
            assertThat(ShippingFeeType.FREE.displayName()).isEqualTo("무료배송");
            assertThat(ShippingFeeType.PAID.displayName()).isEqualTo("유료배송");
            assertThat(ShippingFeeType.CONDITIONAL_FREE.displayName()).isEqualTo("조건부 무료배송");
            assertThat(ShippingFeeType.QUANTITY_BASED.displayName()).isEqualTo("수량별 배송비");
        }
    }
}
