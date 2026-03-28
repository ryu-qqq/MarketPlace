package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistorySortKey;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ClaimHistoryConditionBuilder 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ClaimHistoryConditionBuilder 단위 테스트")
class ClaimHistoryConditionBuilderTest {

    private ClaimHistoryConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ClaimHistoryConditionBuilder();
    }

    // ========================================================================
    // 1. orderItemIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("orderItemIdEq 메서드 테스트")
    class OrderItemIdEqTest {

        @Test
        @DisplayName("orderItemId가 있는 criteria 입력 시 BooleanExpression을 반환합니다")
        void orderItemIdEq_WithValidOrderItemId_ReturnsBooleanExpression() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            1001L, null, QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.orderItemIdEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("claimType 필터 없이도 orderItemId 조건은 항상 반환됩니다")
        void orderItemIdEq_WithoutClaimType_StillReturnsBooleanExpression() {
            // given
            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(1002L);

            // when
            BooleanExpression result = conditionBuilder.orderItemIdEq(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 2. claimTypeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("claimTypeEq 메서드 테스트")
    class ClaimTypeEqTest {

        @Test
        @DisplayName("claimType 필터가 있는 criteria 입력 시 BooleanExpression을 반환합니다")
        void claimTypeEq_WithClaimTypeFilter_ReturnsBooleanExpression() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            1001L,
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.claimTypeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("claimType 필터가 없는 criteria 입력 시 null을 반환합니다")
        void claimTypeEq_WithNoClaimTypeFilter_ReturnsNull() {
            // given
            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(1001L);

            // when
            BooleanExpression result = conditionBuilder.claimTypeEq(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("REFUND 타입 필터 입력 시 BooleanExpression을 반환합니다")
        void claimTypeEq_WithRefundType_ReturnsBooleanExpression() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            1001L,
                            ClaimType.REFUND,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.claimTypeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("EXCHANGE 타입 필터 입력 시 BooleanExpression을 반환합니다")
        void claimTypeEq_WithExchangeType_ReturnsBooleanExpression() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            1001L,
                            ClaimType.EXCHANGE,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.claimTypeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("ORDER 타입 필터 입력 시 BooleanExpression을 반환합니다")
        void claimTypeEq_WithOrderType_ReturnsBooleanExpression() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            1001L,
                            ClaimType.ORDER,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            BooleanExpression result = conditionBuilder.claimTypeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }
}
