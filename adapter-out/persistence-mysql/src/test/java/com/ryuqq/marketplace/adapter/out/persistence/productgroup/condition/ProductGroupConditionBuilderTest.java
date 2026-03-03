package com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
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
 * ProductGroupConditionBuilderTest - 상품 그룹 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupConditionBuilder 단위 테스트")
class ProductGroupConditionBuilderTest {

    private ProductGroupConditionBuilder conditionBuilder;

    @Mock private ProductGroupSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ProductGroupConditionBuilder();
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
    // 3. sellerIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdIn 메서드 테스트")
    class SellerIdInTest {

        @Test
        @DisplayName("유효한 SellerID 목록 입력 시 BooleanExpression을 반환합니다")
        void sellerIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> sellerIds = List.of(1L, 2L);

            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(sellerIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void sellerIdIn_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void sellerIdIn_WithEmpty_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. brandIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("brandIdIn 메서드 테스트")
    class BrandIdInTest {

        @Test
        @DisplayName("유효한 BrandID 목록 입력 시 BooleanExpression을 반환합니다")
        void brandIdIn_WithValidIds_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.brandIdIn(List.of(100L, 200L));

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void brandIdIn_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.brandIdIn(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. categoryIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("categoryIdIn 메서드 테스트")
    class CategoryIdInTest {

        @Test
        @DisplayName("유효한 CategoryID 목록 입력 시 BooleanExpression을 반환합니다")
        void categoryIdIn_WithValidIds_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.categoryIdIn(List.of(200L));

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void categoryIdIn_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.categoryIdIn(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. statusNotDeleted 테스트
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

    // ========================================================================
    // 7. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(List.of(ProductGroupStatus.ACTIVE));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 null을 반환합니다")
        void statusIn_WithoutStatusFilter_ReturnsNull() {
            // given
            given(criteria.hasStatusFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색 조건이 있고 검색 필드가 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField())
                    .willReturn(
                            com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchField
                                    .PRODUCT_GROUP_NAME);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 조건이 있고 검색 필드가 없으면 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithoutSearchField_ReturnsUnifiedSearch() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(false);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_GROUP_ID 필드로 검색 시 ID 조건을 반환합니다")
        void searchCondition_WithProductGroupIdField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField())
                    .willReturn(
                            com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchField
                                    .PRODUCT_GROUP_ID);
            given(criteria.searchWord()).willReturn("123");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("PRODUCT_GROUP_ID 필드에 숫자가 아닌 값 입력 시 null을 반환합니다")
        void searchCondition_WithInvalidProductGroupId_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField())
                    .willReturn(
                            com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchField
                                    .PRODUCT_GROUP_ID);
            given(criteria.searchWord()).willReturn("abc");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithNoCondition_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }
}
