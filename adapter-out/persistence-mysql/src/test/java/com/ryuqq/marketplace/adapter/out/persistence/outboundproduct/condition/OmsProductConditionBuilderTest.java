package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchField;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OmsProductConditionBuilderTest - OMS 상품 조회 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 또는 빈 값 입력 시 null 반환 (동적 쿼리 지원).
 */
@Tag("unit")
@DisplayName("OmsProductConditionBuilder 단위 테스트")
class OmsProductConditionBuilderTest {

    private OmsProductConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new OmsProductConditionBuilder();
    }

    // ========================================================================
    // 1. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("유효한 상태 목록 입력 시 BooleanExpression을 반환합니다")
        void statusIn_WithValidStatuses_ReturnsBooleanExpression() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");

            // when
            BooleanExpression result = conditionBuilder.statusIn(statuses);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("단일 상태 입력 시 BooleanExpression을 반환합니다")
        void statusIn_WithSingleStatus_ReturnsBooleanExpression() {
            // given
            List<String> statuses = List.of("ACTIVE");

            // when
            BooleanExpression result = conditionBuilder.statusIn(statuses);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void statusIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void statusIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.statusIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. sellerIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdIn 메서드 테스트")
    class SellerIdInTest {

        @Test
        @DisplayName("유효한 셀러 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void sellerIdIn_WithValidSellerIds_ReturnsBooleanExpression() {
            // given
            List<Long> sellerIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(sellerIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void sellerIdIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void sellerIdIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. productGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("productGroupIdIn 메서드 테스트")
    class ProductGroupIdInTest {

        @Test
        @DisplayName("유효한 상품그룹 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void productGroupIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> ids = List.of(100L, 200L, 300L);

            // when
            BooleanExpression result = conditionBuilder.productGroupIdIn(ids);

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
    // 4. productGroupNameContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("productGroupNameContains 메서드 테스트")
    class ProductGroupNameContainsTest {

        @Test
        @DisplayName("유효한 키워드 입력 시 BooleanExpression을 반환합니다")
        void productGroupNameContains_WithValidKeyword_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.productGroupNameContains("테스트");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 키워드 입력 시 null을 반환합니다")
        void productGroupNameContains_WithNullKeyword_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupNameContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 키워드 입력 시 null을 반환합니다")
        void productGroupNameContains_WithBlankKeyword_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.productGroupNameContains("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. searchFieldContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchFieldContains 메서드 테스트")
    class SearchFieldContainsTest {

        @Test
        @DisplayName("PRODUCT_NAME 필드와 검색어로 BooleanExpression을 반환합니다")
        void searchFieldContains_WithProductNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(OmsProductSearchField.PRODUCT_NAME, "상품명");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PARTNER_NAME 필드와 검색어로 BooleanExpression을 반환합니다")
        void searchFieldContains_WithPartnerNameField_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(OmsProductSearchField.PARTNER_NAME, "셀러명");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_CODE 필드와 PG- 접두사 코드로 BooleanExpression을 반환합니다")
        void searchFieldContains_WithProductCodeAndPgPrefix_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(
                            OmsProductSearchField.PRODUCT_CODE, "PG-100");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_CODE 필드와 숫자 ID로 BooleanExpression을 반환합니다")
        void searchFieldContains_WithProductCodeAsNumericId_ReturnsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(OmsProductSearchField.PRODUCT_CODE, "100");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_CODE 필드와 숫자가 아닌 코드 입력 시 null을 반환합니다")
        void searchFieldContains_WithProductCodeAsNonNumeric_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(
                            OmsProductSearchField.PRODUCT_CODE, "PG-INVALID");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchField가 null이면 상품명 또는 셀러명 전체 검색 조건을 반환합니다")
        void searchFieldContains_WithNullField_ReturnsAllFieldsExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchFieldContains(null, "검색어");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 null이면 null을 반환합니다")
        void searchFieldContains_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(OmsProductSearchField.PRODUCT_NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 null을 반환합니다")
        void searchFieldContains_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchFieldContains(OmsProductSearchField.PRODUCT_NAME, "   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. createdAtBetween 테스트
    // ========================================================================

    @Nested
    @DisplayName("createdAtBetween 메서드 테스트")
    class CreatedAtBetweenTest {

        @Test
        @DisplayName("시작/종료 시각 모두 입력 시 BooleanExpression을 반환합니다")
        void createdAtBetween_WithBothDates_ReturnsBooleanExpression() {
            // given
            Instant start = Instant.now().minusSeconds(86400);
            Instant end = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.createdAtBetween(start, end);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("시작 시각만 입력 시 goe 조건을 반환합니다")
        void createdAtBetween_WithOnlyStart_ReturnsBooleanExpression() {
            // given
            Instant start = Instant.now().minusSeconds(86400);

            // when
            BooleanExpression result = conditionBuilder.createdAtBetween(start, null);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("종료 시각만 입력 시 loe 조건을 반환합니다")
        void createdAtBetween_WithOnlyEnd_ReturnsBooleanExpression() {
            // given
            Instant end = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.createdAtBetween(null, end);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("시작/종료 시각 모두 null이면 null을 반환합니다")
        void createdAtBetween_WithBothNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.createdAtBetween(null, null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. updatedAtBetween 테스트
    // ========================================================================

    @Nested
    @DisplayName("updatedAtBetween 메서드 테스트")
    class UpdatedAtBetweenTest {

        @Test
        @DisplayName("시작/종료 시각 모두 입력 시 BooleanExpression을 반환합니다")
        void updatedAtBetween_WithBothDates_ReturnsBooleanExpression() {
            // given
            Instant start = Instant.now().minusSeconds(86400);
            Instant end = Instant.now();

            // when
            BooleanExpression result = conditionBuilder.updatedAtBetween(start, end);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("시작/종료 시각 모두 null이면 null을 반환합니다")
        void updatedAtBetween_WithBothNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.updatedAtBetween(null, null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("삭제 제외 조건 BooleanExpression을 반환합니다")
        void notDeleted_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
