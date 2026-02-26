package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerPolicyConditionBuilderTest - 셀러 정책 Composite 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerPolicyConditionBuilder 단위 테스트")
class SellerPolicyConditionBuilderTest {

    private SellerPolicyConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerPolicyConditionBuilder();
    }

    // ========================================================================
    // 1. ShippingPolicy 조건 테스트
    // ========================================================================

    @Nested
    @DisplayName("ShippingPolicy 조건 테스트")
    class ShippingPolicyConditionsTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void shippingPolicySellerIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.shippingPolicySellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void shippingPolicySellerIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicySellerIdEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효한 셀러 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void shippingPolicySellerIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> sellerIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.shippingPolicySellerIdIn(sellerIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 목록 입력 시 null을 반환합니다")
        void shippingPolicySellerIdIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicySellerIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 셀러 ID 목록 입력 시 null을 반환합니다")
        void shippingPolicySellerIdIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.shippingPolicySellerIdIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("삭제되지 않은 배송 정책 조건 BooleanExpression을 반환합니다")
        void shippingPolicyNotDeleted_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicyNotDeleted();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("true 활성 상태 입력 시 BooleanExpression을 반환합니다")
        void shippingPolicyActiveEq_WithTrue_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicyActiveEq(Boolean.TRUE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("false 활성 상태 입력 시 BooleanExpression을 반환합니다")
        void shippingPolicyActiveEq_WithFalse_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicyActiveEq(Boolean.FALSE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 활성 상태 입력 시 null을 반환합니다")
        void shippingPolicyActiveEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicyActiveEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("기본 배송 정책만 조건 BooleanExpression을 반환합니다")
        void shippingPolicyDefaultOnly_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.shippingPolicyDefaultOnly();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 2. RefundPolicy 조건 테스트
    // ========================================================================

    @Nested
    @DisplayName("RefundPolicy 조건 테스트")
    class RefundPolicyConditionsTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void refundPolicySellerIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.refundPolicySellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void refundPolicySellerIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicySellerIdEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효한 셀러 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void refundPolicySellerIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> sellerIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.refundPolicySellerIdIn(sellerIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 목록 입력 시 null을 반환합니다")
        void refundPolicySellerIdIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicySellerIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 셀러 ID 목록 입력 시 null을 반환합니다")
        void refundPolicySellerIdIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.refundPolicySellerIdIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("삭제되지 않은 환불 정책 조건 BooleanExpression을 반환합니다")
        void refundPolicyNotDeleted_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicyNotDeleted();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("true 활성 상태 입력 시 BooleanExpression을 반환합니다")
        void refundPolicyActiveEq_WithTrue_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicyActiveEq(Boolean.TRUE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("false 활성 상태 입력 시 BooleanExpression을 반환합니다")
        void refundPolicyActiveEq_WithFalse_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicyActiveEq(Boolean.FALSE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 활성 상태 입력 시 null을 반환합니다")
        void refundPolicyActiveEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicyActiveEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("기본 환불 정책만 조건 BooleanExpression을 반환합니다")
        void refundPolicyDefaultOnly_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.refundPolicyDefaultOnly();

            // then
            assertThat(result).isNotNull();
        }
    }
}
