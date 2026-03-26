package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductGroupListConditionBuilderTest - 레거시 상품그룹 목록 조회 조건 빌더 단위 테스트.
 *
 * <p>각 조건 메서드는 null/blank 입력 시 null을 반환하고, 유효한 입력 시 BooleanExpression을 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupListConditionBuilder 단위 테스트")
class LegacyProductGroupListConditionBuilderTest {

    private LegacyProductGroupListConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new LegacyProductGroupListConditionBuilder();
    }

    // ========================================================================
    // 1. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("항상 deleteYn = 'N' BooleanExpression을 반환합니다")
        void notDeleted_AlwaysReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 2. betweenTime 테스트
    // ========================================================================

    @Nested
    @DisplayName("betweenTime 메서드 테스트")
    class BetweenTimeTest {

        @Test
        @DisplayName("시작일과 종료일이 모두 존재하면 BooleanExpression을 반환합니다")
        void betweenTime_WithBothDates_ReturnsBooleanExpression() {
            // given
            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

            // when
            BooleanExpression result = conditionBuilder.betweenTime(startDate, endDate);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("시작일이 null이면 null을 반환합니다")
        void betweenTime_WithNullStartDate_ReturnsNull() {
            // given
            LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

            // when
            BooleanExpression result = conditionBuilder.betweenTime(null, endDate);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("종료일이 null이면 null을 반환합니다")
        void betweenTime_WithNullEndDate_ReturnsNull() {
            // given
            LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);

            // when
            BooleanExpression result = conditionBuilder.betweenTime(startDate, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("시작일과 종료일이 모두 null이면 null을 반환합니다")
        void betweenTime_WithBothNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.betweenTime(null, null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. managementTypeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("managementTypeEq 메서드 테스트")
    class ManagementTypeEqTest {

        @Test
        @DisplayName("유효한 관리유형 입력 시 BooleanExpression을 반환합니다")
        void managementTypeEq_WithValidType_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.managementTypeEq("SYSTEM");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 관리유형 입력 시 null을 반환합니다")
        void managementTypeEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.managementTypeEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열 관리유형 입력 시 null을 반환합니다")
        void managementTypeEq_WithBlank_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.managementTypeEq("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. brandEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("brandEq 메서드 테스트")
    class BrandEqTest {

        @Test
        @DisplayName("유효한 브랜드 ID 입력 시 BooleanExpression을 반환합니다")
        void brandEq_WithValidId_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.brandEq(100L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 브랜드 ID 입력 시 null을 반환합니다")
        void brandEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.brandEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 판매자 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidId_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(10L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 판매자 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. soldOutEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("soldOutEq 메서드 테스트")
    class SoldOutEqTest {

        @Test
        @DisplayName("'Y' 입력 시 BooleanExpression을 반환합니다")
        void soldOutEq_WithY_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.soldOutEq("Y");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("'N' 입력 시 BooleanExpression을 반환합니다")
        void soldOutEq_WithN_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.soldOutEq("N");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void soldOutEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.soldOutEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열 입력 시 null을 반환합니다")
        void soldOutEq_WithBlank_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.soldOutEq("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. displayEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("displayEq 메서드 테스트")
    class DisplayEqTest {

        @Test
        @DisplayName("'Y' 입력 시 BooleanExpression을 반환합니다")
        void displayEq_WithY_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.displayEq("Y");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void displayEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.displayEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열 입력 시 null을 반환합니다")
        void displayEq_WithBlank_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.displayEq("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. categoryIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("categoryIn 메서드 테스트")
    class CategoryInTest {

        @Test
        @DisplayName("유효한 카테고리 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void categoryIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> categoryIds = List.of(100L, 200L, 300L);

            // when
            BooleanExpression result = conditionBuilder.categoryIn(categoryIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void categoryIn_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.categoryIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void categoryIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.categoryIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 9. betweenPrice 테스트
    // ========================================================================

    @Nested
    @DisplayName("betweenPrice 메서드 테스트")
    class BetweenPriceTest {

        @Test
        @DisplayName("최소/최대 판매가가 모두 존재하면 between BooleanExpression을 반환합니다")
        void betweenPrice_WithBothPrices_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.betweenPrice(10000L, 50000L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("최소 판매가만 있으면 goe BooleanExpression을 반환합니다")
        void betweenPrice_WithOnlyMinPrice_ReturnsGoeBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.betweenPrice(10000L, null);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("최대 판매가만 있으면 loe BooleanExpression을 반환합니다")
        void betweenPrice_WithOnlyMaxPrice_ReturnsLoeBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.betweenPrice(null, 50000L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("최소/최대 판매가가 모두 null이면 null을 반환합니다")
        void betweenPrice_WithBothNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.betweenPrice(null, null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 10. betweenSalePercent 테스트
    // ========================================================================

    @Nested
    @DisplayName("betweenSalePercent 메서드 테스트")
    class BetweenSalePercentTest {

        @Test
        @DisplayName("최소/최대 할인율이 모두 존재하면 between BooleanExpression을 반환합니다")
        void betweenSalePercent_WithBothRates_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.betweenSalePercent(10L, 50L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("최소 할인율만 있으면 goe BooleanExpression을 반환합니다")
        void betweenSalePercent_WithOnlyMinRate_ReturnsGoeBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.betweenSalePercent(10L, null);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("최대 할인율만 있으면 loe BooleanExpression을 반환합니다")
        void betweenSalePercent_WithOnlyMaxRate_ReturnsLoeBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.betweenSalePercent(null, 50L);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("최소/최대 할인율이 모두 null이면 null을 반환합니다")
        void betweenSalePercent_WithBothNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.betweenSalePercent(null, null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 11. searchKeywordEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchKeywordEq 메서드 테스트")
    class SearchKeywordEqTest {

        @Test
        @DisplayName("PRODUCT_GROUP_NAME 키워드로 검색어 입력 시 contains BooleanExpression을 반환합니다")
        void searchKeywordEq_WithProductGroupName_ReturnsContainsBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchKeywordEq("PRODUCT_GROUP_NAME", "나이키");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_GROUP_ID 키워드로 숫자 검색어 입력 시 eq BooleanExpression을 반환합니다")
        void searchKeywordEq_WithProductGroupIdAndNumericWord_ReturnsEqBooleanExpression() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchKeywordEq("PRODUCT_GROUP_ID", "12345");

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_GROUP_ID 키워드로 숫자가 아닌 검색어 입력 시 null을 반환합니다")
        void searchKeywordEq_WithProductGroupIdAndNonNumericWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchKeywordEq("PRODUCT_GROUP_ID", "not-a-number");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null 검색어 입력 시 null을 반환합니다")
        void searchKeywordEq_WithNullSearchWord_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.searchKeywordEq("PRODUCT_GROUP_NAME", null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열 검색어 입력 시 null을 반환합니다")
        void searchKeywordEq_WithBlankSearchWord_ReturnsNull() {
            // when
            BooleanExpression result =
                    conditionBuilder.searchKeywordEq("PRODUCT_GROUP_NAME", "   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null 키워드로 검색어 입력 시 contains BooleanExpression을 반환합니다")
        void searchKeywordEq_WithNullKeyword_ReturnsContainsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.searchKeywordEq(null, "나이키");

            // then
            assertThat(result).isNotNull();
        }
    }
}
