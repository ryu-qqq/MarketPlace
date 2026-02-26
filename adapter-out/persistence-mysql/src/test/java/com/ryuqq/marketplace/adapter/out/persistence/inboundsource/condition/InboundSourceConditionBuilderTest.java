package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchField;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InboundSourceConditionBuilderTest - InboundSource 조건 빌더 단위 테스트.
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
@DisplayName("InboundSourceConditionBuilder 단위 테스트")
class InboundSourceConditionBuilderTest {

    private InboundSourceConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new InboundSourceConditionBuilder();
    }

    // ========================================================================
    // 1. typeIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("typeIn 메서드 테스트")
    class TypeInTest {

        @Test
        @DisplayName("타입 목록이 있으면 BooleanExpression을 반환합니다")
        void typeIn_WithTypeList_ReturnsBooleanExpression() {
            // given
            List<String> types = List.of("LEGACY");

            // when
            BooleanExpression result = conditionBuilder.typeIn(types);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("여러 타입 목록이 있으면 BooleanExpression을 반환합니다")
        void typeIn_WithMultipleTypes_ReturnsBooleanExpression() {
            // given
            List<String> types = List.of("LEGACY", "CRAWLING", "PARTNER");

            // when
            BooleanExpression result = conditionBuilder.typeIn(types);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("빈 타입 목록이면 null을 반환합니다")
        void typeIn_WithEmptyList_ReturnsNull() {
            // given
            List<String> types = List.of();

            // when
            BooleanExpression result = conditionBuilder.typeIn(types);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null 타입 목록이면 null을 반환합니다")
        void typeIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.typeIn(null);

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
        @DisplayName("빈 상태 목록이면 null을 반환합니다")
        void statusIn_WithEmptyList_ReturnsNull() {
            // given
            List<String> statuses = List.of();

            // when
            BooleanExpression result = conditionBuilder.statusIn(statuses);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null 상태 목록이면 null을 반환합니다")
        void statusIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(null);

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
        @DisplayName("검색어가 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchWord_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchCondition(null, "세토프");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 없으면 null을 반환합니다")
        void searchCondition_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.searchCondition(null, "");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null 검색어이면 null을 반환합니다")
        void searchCondition_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.searchCondition(null, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("CODE 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCodeField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(InboundSourceSearchField.CODE, "SETOF");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(InboundSourceSearchField.NAME, "세토프");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 필드가 null이면 이름과 코드 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithoutSearchField_ReturnsUnifiedSearchExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchCondition(null, "세토프");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("공백 검색어이면 null을 반환합니다")
        void searchCondition_WithWhitespaceSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchCondition(InboundSourceSearchField.NAME, "   ");

            // then
            assertThat(result).isNull();
        }
    }
}
