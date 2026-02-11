package com.ryuqq.marketplace.application.saleschannelcategory.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannelcategory.SalesChannelCategoryQueryFixtures;
import com.ryuqq.marketplace.application.saleschannelcategory.assembler.SalesChannelCategoryAssembler;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.factory.SalesChannelCategoryQueryFactory;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import java.util.Collections;
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
@DisplayName("SearchSalesChannelCategoryByOffsetService 단위 테스트")
class SearchSalesChannelCategoryByOffsetServiceTest {

    @InjectMocks private SearchSalesChannelCategoryByOffsetService sut;

    @Mock private SalesChannelCategoryReadManager readManager;
    @Mock private SalesChannelCategoryQueryFactory queryFactory;
    @Mock private SalesChannelCategoryAssembler assembler;

    @Nested
    @DisplayName("execute() - 외부 채널 카테고리 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 외부 채널 카테고리 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(0, 20);
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            List<SalesChannelCategory> categories =
                    List.of(
                            SalesChannelCategoryFixtures.activeSalesChannelCategory(1L),
                            SalesChannelCategoryFixtures.activeSalesChannelCategory(2L));
            long totalElements = 2L;

            SalesChannelCategoryPageResult expected =
                    SalesChannelCategoryQueryFixtures.salesChannelCategoryPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(categories, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).hasSize(2);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(categories, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(0, 20);
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            List<SalesChannelCategory> emptyCategories = Collections.emptyList();
            long totalElements = 0L;

            SalesChannelCategoryPageResult expected =
                    SalesChannelCategoryQueryFixtures.emptySalesChannelCategoryPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyCategories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    emptyCategories, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("판매채널 ID 필터가 적용된 검색을 수행한다")
        void execute_WithSalesChannelIdFilter_FiltersResults() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(salesChannelIds);
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            List<SalesChannelCategory> categories =
                    List.of(SalesChannelCategoryFixtures.activeSalesChannelCategory(1L));
            long totalElements = 1L;

            SalesChannelCategoryPageResult expected =
                    SalesChannelCategoryQueryFixtures.salesChannelCategoryPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(categories, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isNotEmpty();
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("상태 필터가 적용된 검색을 수행한다")
        void execute_WithStatusFilter_FiltersResults() {
            // given
            List<String> statuses = List.of("ACTIVE");
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(null, statuses, null, null);
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            List<SalesChannelCategory> categories =
                    List.of(SalesChannelCategoryFixtures.activeSalesChannelCategory());
            long totalElements = 1L;

            SalesChannelCategoryPageResult expected =
                    SalesChannelCategoryQueryFixtures.salesChannelCategoryPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(categories, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isNotEmpty();
            then(queryFactory).should().createCriteria(params);
        }
    }
}
