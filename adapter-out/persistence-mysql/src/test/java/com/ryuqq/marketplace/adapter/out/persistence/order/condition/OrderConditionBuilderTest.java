package com.ryuqq.marketplace.adapter.out.persistence.order.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrderConditionBuilder 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("OrderConditionBuilder 단위 테스트")
class OrderConditionBuilderTest {

    private OrderConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new OrderConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 orderId 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidOrderId_ReturnsBooleanExpression() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012345";

            // when
            BooleanExpression result = conditionBuilder.idEq(orderId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null orderId 입력 시 null을 반환합니다")
        void idEq_WithNullOrderId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. orderNumberEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("orderNumberEq 메서드 테스트")
    class OrderNumberEqTest {

        @Test
        @DisplayName("유효한 orderNumber 입력 시 BooleanExpression을 반환합니다")
        void orderNumberEq_WithValidOrderNumber_ReturnsBooleanExpression() {
            // given
            String orderNumber = "ORD-20260302-0001";

            // when
            BooleanExpression result = conditionBuilder.orderNumberEq(orderNumber);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null orderNumber 입력 시 null을 반환합니다")
        void orderNumberEq_WithNullOrderNumber_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.orderNumberEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. salesChannelIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("salesChannelIdEq 메서드 테스트")
    class SalesChannelIdEqTest {

        @Test
        @DisplayName("유효한 salesChannelId 입력 시 BooleanExpression을 반환합니다")
        void salesChannelIdEq_WithValidSalesChannelId_ReturnsBooleanExpression() {
            // given
            long salesChannelId = 1L;

            // when
            BooleanExpression result = conditionBuilder.salesChannelIdEq(salesChannelId);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. externalOrderNoEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("externalOrderNoEq 메서드 테스트")
    class ExternalOrderNoEqTest {

        @Test
        @DisplayName("유효한 externalOrderNo 입력 시 BooleanExpression을 반환합니다")
        void externalOrderNoEq_WithValidExternalOrderNo_ReturnsBooleanExpression() {
            // given
            String externalOrderNo = "EXT-ORD-001";

            // when
            BooleanExpression result = conditionBuilder.externalOrderNoEq(externalOrderNo);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null externalOrderNo 입력 시 null을 반환합니다")
        void externalOrderNoEq_WithNullExternalOrderNo_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.externalOrderNoEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("deletedAt IS NULL 조건 BooleanExpression을 반환합니다")
        void notDeleted_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
