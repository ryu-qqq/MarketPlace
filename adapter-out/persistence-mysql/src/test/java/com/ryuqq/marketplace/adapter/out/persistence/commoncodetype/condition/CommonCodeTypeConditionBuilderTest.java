package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchField;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CommonCodeTypeConditionBuilderTest - 공통 코드 타입 조건 빌더 단위 테스트.
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
@DisplayName("CommonCodeTypeConditionBuilder 단위 테스트")
class CommonCodeTypeConditionBuilderTest {

    private CommonCodeTypeConditionBuilder conditionBuilder;

    @Mock private CommonCodeTypeSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CommonCodeTypeConditionBuilder();
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
    // 3. idNe 테스트
    // ========================================================================

    @Nested
    @DisplayName("idNe 메서드 테스트")
    class IdNeTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idNe_WithValidId_ReturnsBooleanExpression() {
            // given
            Long excludeId = 1L;

            // when
            BooleanExpression result = conditionBuilder.idNe(excludeId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idNe_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idNe(null);

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
            String code = "PAYMENT_METHOD";

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
    }

    // ========================================================================
    // 5. displayOrderEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("displayOrderEq 메서드 테스트")
    class DisplayOrderEqTest {

        @Test
        @DisplayName("유효한 순서 입력 시 BooleanExpression을 반환합니다")
        void displayOrderEq_WithValidOrder_ReturnsBooleanExpression() {
            // given
            Integer displayOrder = 1;

            // when
            BooleanExpression result = conditionBuilder.displayOrderEq(displayOrder);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 순서 입력 시 null을 반환합니다")
        void displayOrderEq_WithNullOrder_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.displayOrderEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. activeEq (Boolean) 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq (Boolean) 메서드 테스트")
    class ActiveEqBooleanTest {

        @Test
        @DisplayName("true 입력 시 BooleanExpression을 반환합니다")
        void activeEq_WithTrue_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(Boolean.TRUE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("false 입력 시 BooleanExpression을 반환합니다")
        void activeEq_WithFalse_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.activeEq(Boolean.FALSE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void activeEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.activeEq((Boolean) null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. activeEq (Criteria) 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq (Criteria) 메서드 테스트")
    class ActiveEqCriteriaTest {

        @Test
        @DisplayName("필터가 있으면 BooleanExpression을 반환합니다")
        void activeEq_WithActiveFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasActiveFilter()).willReturn(true);
            given(criteria.active()).willReturn(true);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("필터가 없으면 null을 반환합니다")
        void activeEq_WithoutActiveFilter_ReturnsNull() {
            // given
            given(criteria.hasActiveFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. searchFieldContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchFieldContains 메서드 테스트")
    class SearchFieldContainsTest {

        @Test
        @DisplayName("유효한 검색어 입력 시 (null 필드) BooleanExpression을 반환합니다")
        void searchFieldContains_WithNullField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchFieldContains(null, "결제");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("CODE 필드 + 검색어 입력 시 BooleanExpression을 반환합니다")
        void searchFieldContains_WithCodeField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(CommonCodeTypeSearchField.CODE, "결제");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(CommonCodeTypeSearchField.CODE, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 검색어 입력 시 null을 반환합니다")
        void searchFieldContains_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(CommonCodeTypeSearchField.NAME, "   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 9. searchCondition (Criteria) 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition (Criteria) 메서드 테스트")
    class SearchConditionCriteriaTest {

        @Test
        @DisplayName("검색어가 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchWord_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchWord()).willReturn(true);
            given(criteria.searchField()).willReturn(CommonCodeTypeSearchField.CODE);
            given(criteria.searchWord()).willReturn("결제");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchWord_ReturnsNull() {
            // given
            given(criteria.hasSearchWord()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 10. typeHasCommonCodeValue 테스트
    // ========================================================================

    @Nested
    @DisplayName("typeHasCommonCodeValue 메서드 테스트")
    class TypeHasCommonCodeValueTest {

        @Test
        @DisplayName("유효한 type 입력 시 BooleanExpression을 반환합니다")
        void typeHasCommonCodeValue_WithValidType_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.typeHasCommonCodeValue("CARD");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null type 입력 시 null을 반환합니다")
        void typeHasCommonCodeValue_WithNullType_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.typeHasCommonCodeValue((String) null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("type이 있으면 (Criteria) BooleanExpression을 반환합니다")
        void typeHasCommonCodeValue_WithCriteria_ReturnsBooleanExpression() {
            // given
            given(criteria.hasType()).willReturn(true);
            given(criteria.type()).willReturn("CARD");

            // when
            BooleanExpression result = conditionBuilder.typeHasCommonCodeValue(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("type이 없으면 (Criteria) null을 반환합니다")
        void typeHasCommonCodeValue_WithoutType_ReturnsNull() {
            // given
            given(criteria.hasType()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.typeHasCommonCodeValue(criteria);

            // then
            assertThat(result).isNull();
        }
    }
}
