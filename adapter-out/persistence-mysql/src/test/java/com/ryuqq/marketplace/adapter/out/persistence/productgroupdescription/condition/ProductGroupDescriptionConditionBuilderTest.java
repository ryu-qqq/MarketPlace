package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductGroupDescriptionConditionBuilderTest - 상품 그룹 상세설명 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (QueryDSL에서 where 조건 무시).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductGroupDescriptionConditionBuilder 단위 테스트")
class ProductGroupDescriptionConditionBuilderTest {

    private ProductGroupDescriptionConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ProductGroupDescriptionConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 id 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null id 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. productGroupIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("productGroupIdEq 메서드 테스트")
    class ProductGroupIdEqTest {

        @Test
        @DisplayName("유효한 productGroupId 입력 시 BooleanExpression을 반환합니다")
        void productGroupIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long productGroupId = 10L;

            // when
            BooleanExpression result = conditionBuilder.productGroupIdEq(productGroupId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null productGroupId 입력 시 null을 반환합니다")
        void productGroupIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }
}
