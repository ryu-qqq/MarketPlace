package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchField;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySortKey;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SalesChannelCategoryConditionBuilderTest - SalesChannelCategory QueryDSL 조건 빌더 단위 테스트.
 *
 * <p>PER-COND-001: ConditionBuilder는 QueryDSL 조건 생성만 담당.
 *
 * <p>PER-COND-002: null 안전성 보장 (null 입력 시 null 반환).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SalesChannelCategoryConditionBuilder 단위 테스트")
class SalesChannelCategoryConditionBuilderTest {

    private SalesChannelCategoryConditionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new SalesChannelCategoryConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("ID가 주어지면 ID 동등 조건을 생성합니다")
        void idEq_WithValidId_CreatesCondition() {
            // given
            Long id = 1L;

            // when
            BooleanExpression condition = builder.idEq(id);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString()).contains("salesChannelCategoryJpaEntity.id = 1");
        }

        @Test
        @DisplayName("ID가 null이면 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression condition = builder.idEq(null);

            // then
            assertThat(condition).isNull();
        }
    }

    // ========================================================================
    // 2. salesChannelIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("salesChannelIdEq 메서드 테스트")
    class SalesChannelIdEqTest {

        @Test
        @DisplayName("SalesChannelId가 주어지면 동등 조건을 생성합니다")
        void salesChannelIdEq_WithValidId_CreatesCondition() {
            // given
            Long salesChannelId = 1L;

            // when
            BooleanExpression condition = builder.salesChannelIdEq(salesChannelId);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString())
                    .contains("salesChannelCategoryJpaEntity.salesChannelId = 1");
        }

        @Test
        @DisplayName("SalesChannelId가 null이면 null을 반환합니다")
        void salesChannelIdEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression condition = builder.salesChannelIdEq(null);

            // then
            assertThat(condition).isNull();
        }
    }

    // ========================================================================
    // 3. salesChannelIdsIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("salesChannelIdsIn 메서드 테스트")
    class SalesChannelIdsInTest {

        @Test
        @DisplayName("SalesChannelIds가 주어지면 IN 조건을 생성합니다")
        void salesChannelIdsIn_WithValidIds_CreatesCondition() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression condition = builder.salesChannelIdsIn(salesChannelIds);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString())
                    .contains("salesChannelCategoryJpaEntity.salesChannelId in [1, 2, 3]");
        }

        @Test
        @DisplayName("빈 컬렉션이 주어지면 null을 반환합니다")
        void salesChannelIdsIn_WithEmptyCollection_ReturnsNull() {
            // given
            List<Long> emptyList = List.of();

            // when
            BooleanExpression condition = builder.salesChannelIdsIn(emptyList);

            // then
            assertThat(condition).isNull();
        }

        @Test
        @DisplayName("null이 주어지면 null을 반환합니다")
        void salesChannelIdsIn_WithNull_ReturnsNull() {
            // when
            BooleanExpression condition = builder.salesChannelIdsIn(null);

            // then
            assertThat(condition).isNull();
        }
    }

    // ========================================================================
    // 4. externalCategoryCodeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("externalCategoryCodeEq 메서드 테스트")
    class ExternalCategoryCodeEqTest {

        @Test
        @DisplayName("ExternalCategoryCode가 주어지면 동등 조건을 생성합니다")
        void externalCategoryCodeEq_WithValidCode_CreatesCondition() {
            // given
            String code = "CAT001";

            // when
            BooleanExpression condition = builder.externalCategoryCodeEq(code);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString())
                    .contains("salesChannelCategoryJpaEntity.externalCategoryCode = CAT001");
        }

        @Test
        @DisplayName("ExternalCategoryCode가 null이면 null을 반환합니다")
        void externalCategoryCodeEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression condition = builder.externalCategoryCodeEq(null);

            // then
            assertThat(condition).isNull();
        }
    }

    // ========================================================================
    // 5. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("Status 필터가 있으면 IN 조건을 생성합니다")
        void statusIn_WithStatusFilter_CreatesCondition() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            List.of(
                                    SalesChannelCategoryStatus.ACTIVE,
                                    SalesChannelCategoryStatus.INACTIVE),
                            null,
                            null,
                            queryContext);

            // when
            BooleanExpression condition = builder.statusIn(criteria);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString()).contains("salesChannelCategoryJpaEntity.status in ");
        }

        @Test
        @DisplayName("Status 필터가 없으면 null을 반환합니다")
        void statusIn_WithoutStatusFilter_ReturnsNull() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            // when
            BooleanExpression condition = builder.statusIn(criteria);

            // then
            assertThat(condition).isNull();
        }
    }

    // ========================================================================
    // 6. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색어만 있고 필드가 없으면 OR 조건을 생성합니다")
        void searchCondition_WithWordOnly_CreatesOrCondition() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, "테스트", queryContext);

            // when
            BooleanExpression condition = builder.searchCondition(criteria);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString()).contains("like");
            assertThat(condition.toString()).contains("or");
        }

        @Test
        @DisplayName("EXTERNAL_CODE 필드로 검색하면 Code 조건을 생성합니다")
        void searchCondition_WithExternalCodeField_CreatesCodeCondition() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            SalesChannelCategorySearchField.EXTERNAL_CODE,
                            "CAT001",
                            queryContext);

            // when
            BooleanExpression condition = builder.searchCondition(criteria);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString())
                    .contains("salesChannelCategoryJpaEntity.externalCategoryCode like");
        }

        @Test
        @DisplayName("EXTERNAL_NAME 필드로 검색하면 Name 조건을 생성합니다")
        void searchCondition_WithExternalNameField_CreatesNameCondition() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            SalesChannelCategorySearchField.EXTERNAL_NAME,
                            "테스트",
                            queryContext);

            // when
            BooleanExpression condition = builder.searchCondition(criteria);

            // then
            assertThat(condition).isNotNull();
            assertThat(condition.toString())
                    .contains("salesChannelCategoryJpaEntity.externalCategoryName like");
        }

        @Test
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchCondition_ReturnsNull() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            // when
            BooleanExpression condition = builder.searchCondition(criteria);

            // then
            assertThat(condition).isNull();
        }
    }

    // ========================================================================
    // 7. 복합 조건 테스트
    // ========================================================================

    @Nested
    @DisplayName("복합 조건 테스트")
    class CombinedConditionTest {

        @Test
        @DisplayName("여러 조건을 AND로 결합할 수 있습니다")
        void combinedCondition_WithMultipleConditions_CombinesWithAnd() {
            // given
            Long salesChannelId = 1L;
            String externalCode = "CAT001";

            // when
            BooleanExpression condition1 = builder.salesChannelIdEq(salesChannelId);
            BooleanExpression condition2 = builder.externalCategoryCodeEq(externalCode);
            BooleanExpression combined = condition1.and(condition2);

            // then
            assertThat(combined).isNotNull();
            assertThat(combined.toString()).contains("salesChannelId = 1");
            assertThat(combined.toString()).contains("externalCategoryCode = CAT001");
        }
    }
}
