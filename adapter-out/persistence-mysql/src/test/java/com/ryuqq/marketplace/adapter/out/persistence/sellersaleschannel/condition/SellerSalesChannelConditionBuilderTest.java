package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerSalesChannelConditionBuilderTest - 셀러 판매채널 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerSalesChannelConditionBuilder 단위 테스트")
class SellerSalesChannelConditionBuilderTest {

    private SellerSalesChannelConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerSalesChannelConditionBuilder();
    }

    // ========================================================================
    // 1. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 sellerId 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null sellerId 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("다른 sellerId 입력 시 각각 다른 BooleanExpression을 반환합니다")
        void sellerIdEq_WithDifferentIds_ReturnsDistinctExpressions() {
            // when
            BooleanExpression result1 = conditionBuilder.sellerIdEq(1L);
            BooleanExpression result2 = conditionBuilder.sellerIdEq(2L);

            // then
            assertThat(result1).isNotNull();
            assertThat(result2).isNotNull();
            assertThat(result1).isNotEqualTo(result2);
        }
    }

    // ========================================================================
    // 2. connectionStatusConnected 테스트
    // ========================================================================

    @Nested
    @DisplayName("connectionStatusConnected 메서드 테스트")
    class ConnectionStatusConnectedTest {

        @Test
        @DisplayName("CONNECTED 상태 조건을 나타내는 BooleanExpression을 반환합니다")
        void connectionStatusConnected_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.connectionStatusConnected();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("connectionStatusConnected는 항상 null이 아닌 BooleanExpression을 반환합니다")
        void connectionStatusConnected_AlwaysReturnsNonNull() {
            // when
            BooleanExpression first = conditionBuilder.connectionStatusConnected();
            BooleanExpression second = conditionBuilder.connectionStatusConnected();

            // then
            assertThat(first).isNotNull();
            assertThat(second).isNotNull();
        }
    }
}
