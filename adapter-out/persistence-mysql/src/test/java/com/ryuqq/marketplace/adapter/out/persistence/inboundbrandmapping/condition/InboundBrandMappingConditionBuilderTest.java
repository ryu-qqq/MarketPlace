package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchField;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InboundBrandMappingConditionBuilderTest - InboundBrandMapping 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InboundBrandMappingConditionBuilder 단위 테스트")
class InboundBrandMappingConditionBuilderTest {

    private InboundBrandMappingConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new InboundBrandMappingConditionBuilder();
    }

    // ========================================================================
    // 1. inboundSourceIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("inboundSourceIdEq 메서드 테스트")
    class InboundSourceIdEqTest {

        @Test
        @DisplayName("유효한 inboundSourceId 입력 시 BooleanExpression을 반환합니다")
        void inboundSourceIdEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long inboundSourceId = 1L;

            // when
            BooleanExpression result = conditionBuilder.inboundSourceIdEq(inboundSourceId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null inboundSourceId 입력 시 null을 반환합니다")
        void inboundSourceIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.inboundSourceIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("상태 목록이 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithStatusList_ReturnsBooleanExpression() {
            // given
            List<String> statuses = List.of("ACTIVE");

            // when
            BooleanExpression result = conditionBuilder.statusIn(statuses);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("여러 상태 목록이 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithMultipleStatuses_ReturnsBooleanExpression() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");

            // when
            BooleanExpression result = conditionBuilder.statusIn(statuses);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 상태 목록 입력 시 null을 반환합니다")
        void statusIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 상태 목록 입력 시 null을 반환합니다")
        void statusIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(List.of());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색어가 있고 searchField가 null이면 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchWordAndNullField_ReturnsUnifiedExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchCondition(null, "테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("EXTERNAL_CODE 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithExternalCodeField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(
                            InboundBrandMappingSearchField.EXTERNAL_CODE, "BR001");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("EXTERNAL_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithExternalNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(
                            InboundBrandMappingSearchField.EXTERNAL_NAME, "나이키");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 null이면 null을 반환합니다")
        void searchCondition_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(
                            InboundBrandMappingSearchField.EXTERNAL_CODE, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("검색어가 공백이면 null을 반환합니다")
        void searchCondition_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(
                            InboundBrandMappingSearchField.EXTERNAL_NAME, "   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("검색어와 searchField가 모두 null이면 null을 반환합니다")
        void searchCondition_WithNullFieldAndNullWord_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.searchCondition(null, null);

            // then
            assertThat(result).isNull();
        }
    }
}
