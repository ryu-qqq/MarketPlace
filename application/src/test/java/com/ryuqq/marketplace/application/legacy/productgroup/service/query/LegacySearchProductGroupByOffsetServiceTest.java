package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.productgroup.factory.LegacyProductGroupQueryFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupListReadFacade;
import com.ryuqq.marketplace.application.legacy.shared.assembler.LegacyProductGroupAssembler;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacySearchProductGroupByOffsetService 단위 테스트")
class LegacySearchProductGroupByOffsetServiceTest {

    @InjectMocks private LegacySearchProductGroupByOffsetService sut;

    @Mock private LegacyProductGroupListReadFacade readFacade;
    @Mock private LegacyProductGroupQueryFactory queryFactory;
    @Mock private LegacyProductGroupAssembler assembler;
    @Mock private CategoryReadManager categoryReadManager;

    @Nested
    @DisplayName("execute() - 레거시 상품그룹 목록 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("카테고리 ID 없는 파라미터로 목록을 조회하고 PageResult를 반환한다")
        void execute_ParamsWithoutCategoryId_ReturnsPageResult() {
            // given
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            List<LegacyProductGroupDetailBundle> bundles =
                    List.of(LegacyProductGroupQueryFixtures.detailBundle(1L));
            long totalElements = 1L;
            LegacyProductGroupPageResult expected = LegacyProductGroupQueryFixtures.pageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getBundles(criteria)).willReturn(bundles);
            given(readFacade.count(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(bundles, totalElements, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            LegacyProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expected);
            then(queryFactory).should().createCriteria(params);
            then(readFacade).should().getBundles(criteria);
            then(readFacade).should().count(criteria);
            then(assembler)
                    .should()
                    .toPageResult(bundles, totalElements, criteria.page(), criteria.size());
        }

        @Test
        @DisplayName("카테고리 ID가 없으면 CategoryReadManager를 호출하지 않는다")
        void execute_ParamsWithoutCategoryId_SkipsCategoryExpansion() {
            // given
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            List<LegacyProductGroupDetailBundle> bundles = List.of();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getBundles(criteria)).willReturn(bundles);
            given(readFacade.count(criteria)).willReturn(0L);
            given(assembler.toPageResult(bundles, 0L, criteria.page(), criteria.size()))
                    .willReturn(
                            LegacyProductGroupPageResult.empty(criteria.page(), criteria.size()));

            // when
            sut.execute(params);

            // then
            then(categoryReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("카테고리 ID가 있으면 하위 카테고리까지 확장하여 검색한다")
        void execute_ParamsWithCategoryId_ExpandsDescendantCategories() {
            // given
            List<Long> originalCategoryIds = List.of(200L);
            List<Long> expandedCategoryIds = List.of(200L, 201L, 202L);
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(200L);
            LegacyProductGroupSearchParams expandedParams =
                    params.withCategoryIds(expandedCategoryIds);
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.criteriaWithCategoryIds(expandedCategoryIds);
            List<LegacyProductGroupDetailBundle> bundles =
                    List.of(LegacyProductGroupQueryFixtures.detailBundle(1L));
            long totalElements = 1L;
            LegacyProductGroupPageResult expected = LegacyProductGroupQueryFixtures.pageResult();

            given(categoryReadManager.expandWithDescendants(originalCategoryIds))
                    .willReturn(expandedCategoryIds);
            given(queryFactory.createCriteria(expandedParams)).willReturn(criteria);
            given(readFacade.getBundles(criteria)).willReturn(bundles);
            given(readFacade.count(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(bundles, totalElements, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            LegacyProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            then(categoryReadManager).should().expandWithDescendants(originalCategoryIds);
            then(queryFactory).should().createCriteria(expandedParams);
        }

        @Test
        @DisplayName("카테고리 확장 후 확장된 Criteria로 ReadFacade를 호출한다")
        void execute_AfterCategoryExpansion_CallsReadFacadeWithExpandedCriteria() {
            // given
            List<Long> originalCategoryIds = List.of(100L);
            List<Long> expandedCategoryIds = List.of(100L, 110L, 120L);
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithCategory(100L);
            LegacyProductGroupSearchParams expandedParams =
                    params.withCategoryIds(expandedCategoryIds);
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.criteriaWithCategoryIds(expandedCategoryIds);

            given(categoryReadManager.expandWithDescendants(originalCategoryIds))
                    .willReturn(expandedCategoryIds);
            given(queryFactory.createCriteria(expandedParams)).willReturn(criteria);
            given(readFacade.getBundles(criteria)).willReturn(List.of());
            given(readFacade.count(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), 0L, criteria.page(), criteria.size()))
                    .willReturn(
                            LegacyProductGroupPageResult.empty(criteria.page(), criteria.size()));

            // when
            sut.execute(params);

            // then
            then(readFacade).should().getBundles(criteria);
            then(readFacade).should().count(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 empty PageResult를 반환한다")
        void execute_EmptyResult_ReturnsEmptyPageResult() {
            // given
            LegacyProductGroupSearchParams params = LegacyProductGroupQueryFixtures.searchParams();
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();
            LegacyProductGroupPageResult expected =
                    LegacyProductGroupQueryFixtures.emptyPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getBundles(criteria)).willReturn(List.of());
            given(readFacade.count(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), 0L, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            LegacyProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("sellerId 필터로 검색하면 Criteria에 sellerId가 포함된다")
        void execute_WithSellerFilter_PassesSellerIdToCriteria() {
            // given
            Long sellerId = 1L;
            LegacyProductGroupSearchParams params =
                    LegacyProductGroupQueryFixtures.searchParamsWithSeller(sellerId);
            LegacyProductGroupSearchCriteria criteria =
                    LegacyProductGroupQueryFixtures.defaultCriteria();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getBundles(criteria)).willReturn(List.of());
            given(readFacade.count(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), 0L, criteria.page(), criteria.size()))
                    .willReturn(LegacyProductGroupQueryFixtures.emptyPageResult());

            // when
            sut.execute(params);

            // then
            then(queryFactory).should().createCriteria(params);
        }
    }
}
