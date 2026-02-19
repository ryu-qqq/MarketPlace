package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerOperationConditionBuilderTest - 셀러 운영 메타데이터 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerOperationConditionBuilder 단위 테스트")
class SellerOperationConditionBuilderTest {

    private SellerOperationConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerOperationConditionBuilder();
    }

    // ========================================================================
    // 1. addressSellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("addressSellerIdEq 메서드 테스트")
    class AddressSellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void addressSellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.addressSellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void addressSellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.addressSellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. addressNotDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("addressNotDeleted 메서드 테스트")
    class AddressNotDeletedTest {

        @Test
        @DisplayName("항상 삭제되지 않은 주소 조건 BooleanExpression을 반환합니다")
        void addressNotDeleted_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.addressNotDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
