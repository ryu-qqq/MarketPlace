package com.ryuqq.marketplace.adapter.out.persistence.product.condition;

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
 * ProductConditionBuilderTest - 상품 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductConditionBuilder 단위 테스트")
class ProductConditionBuilderTest {

    private ProductConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ProductConditionBuilder();
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
    // 2. idIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("idIn 메서드 테스트")
    class IdInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void idIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.idIn(ids);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void idIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void idIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. productGroupIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("productGroupIdEq 메서드 테스트")
    class ProductGroupIdEqTest {

        @Test
        @DisplayName("유효한 ProductGroupId 입력 시 BooleanExpression을 반환합니다")
        void productGroupIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long productGroupId = 10L;

            // when
            BooleanExpression result = conditionBuilder.productGroupIdEq(productGroupId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ProductGroupId 입력 시 null을 반환합니다")
        void productGroupIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. productGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("productGroupIdIn 메서드 테스트")
    class ProductGroupIdInTest {

        @Test
        @DisplayName("유효한 productGroupId 목록 입력 시 BooleanExpression을 반환합니다")
        void productGroupIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> productGroupIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.productGroupIdIn(productGroupIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void productGroupIdIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void productGroupIdIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupIdIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. statusNotDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusNotDeleted 메서드 테스트")
    class StatusNotDeletedTest {

        @Test
        @DisplayName("statusNotDeleted는 항상 BooleanExpression을 반환합니다")
        void statusNotDeleted_AlwaysReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.statusNotDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
