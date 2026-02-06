package com.ryuqq.marketplace.adapter.out.persistence.commoncode.condition;

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
 * CommonCodeConditionBuilderTest - 공통 코드 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CommonCodeConditionBuilder 단위 테스트")
class CommonCodeConditionBuilderTest {

    private CommonCodeConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CommonCodeConditionBuilder();
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
    // 3. commonCodeTypeIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("commonCodeTypeIdEq 메서드 테스트")
    class CommonCodeTypeIdEqTest {

        @Test
        @DisplayName("유효한 타입 ID 입력 시 BooleanExpression을 반환합니다")
        void commonCodeTypeIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long commonCodeTypeId = 1L;

            // when
            BooleanExpression result = conditionBuilder.commonCodeTypeIdEq(commonCodeTypeId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 타입 ID 입력 시 null을 반환합니다")
        void commonCodeTypeIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.commonCodeTypeIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. codeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("codeEq 메서드 테스트")
    class CodeEqTest {

        @Test
        @DisplayName("유효한 코드 입력 시 BooleanExpression을 반환합니다")
        void codeEq_WithValidCode_ReturnsBooleanExpression() {
            // given
            String code = "CREDIT_CARD";

            // when
            BooleanExpression result = conditionBuilder.codeEq(code);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 코드 입력 시 null을 반환합니다")
        void codeEq_WithNullCode_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.codeEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 코드 입력 시 null을 반환합니다")
        void codeEq_WithBlankCode_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.codeEq("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. codeContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("codeContains 메서드 테스트")
    class CodeContainsTest {

        @Test
        @DisplayName("유효한 코드 입력 시 BooleanExpression을 반환합니다")
        void codeContains_WithValidCode_ReturnsBooleanExpression() {
            // given
            String code = "CARD";

            // when
            BooleanExpression result = conditionBuilder.codeContains(code);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 코드 입력 시 null을 반환합니다")
        void codeContains_WithNullCode_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.codeContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 코드 입력 시 null을 반환합니다")
        void codeContains_WithBlankCode_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.codeContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. activeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq 메서드 테스트")
    class ActiveEqTest {

        @Test
        @DisplayName("true 입력 시 BooleanExpression을 반환합니다")
        void activeEq_WithTrue_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(true);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("false 입력 시 BooleanExpression을 반환합니다")
        void activeEq_WithFalse_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(false);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void activeEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("항상 BooleanExpression을 반환합니다")
        void notDeleted_AlwaysReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
