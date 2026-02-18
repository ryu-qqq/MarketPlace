package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity.ContractStatusJpaValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerContractConditionBuilderTest - 셀러 계약 정보 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerContractConditionBuilder 단위 테스트")
class SellerContractConditionBuilderTest {

    private SellerContractConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerContractConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. statusEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusEq 메서드 테스트")
    class StatusEqTest {

        @Test
        @DisplayName("유효한 계약 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithValidStatus_ReturnsBooleanExpression() {
            // given
            ContractStatusJpaValue status = ContractStatusJpaValue.ACTIVE;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("TERMINATED 계약 상태 입력 시 BooleanExpression을 반환합니다")
        void statusEq_WithTerminatedStatus_ReturnsBooleanExpression() {
            // given
            ContractStatusJpaValue status = ContractStatusJpaValue.TERMINATED;

            // when
            BooleanExpression result = conditionBuilder.statusEq(status);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 상태 입력 시 null을 반환합니다")
        void statusEq_WithNullStatus_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusEq(null);

            // then
            assertThat(result).isNull();
        }
    }
}
