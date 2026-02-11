package com.ryuqq.marketplace.application.categorypreset.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetQueryFixtures;
import com.ryuqq.marketplace.application.categorypreset.assembler.CategoryPresetAssembler;
import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetQueryFactory;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
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
@DisplayName("SearchCategoryPresetByOffsetService 단위 테스트")
class SearchCategoryPresetByOffsetServiceTest {

    @InjectMocks private SearchCategoryPresetByOffsetService sut;

    @Mock private CategoryPresetReadManager readManager;
    @Mock private CategoryPresetQueryFactory queryFactory;
    @Mock private CategoryPresetAssembler assembler;

    @Nested
    @DisplayName("execute() - 카테고리 프리셋 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 카테고리 프리셋 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            CategoryPresetSearchParams params = CategoryPresetQueryFixtures.searchParams(0, 20);
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();
            List<CategoryPresetResult> results =
                    List.of(
                            CategoryPresetQueryFixtures.categoryPresetResult(1L),
                            CategoryPresetQueryFixtures.categoryPresetResult(2L));
            long totalCount = 2L;
            CategoryPresetPageResult expected =
                    CategoryPresetPageResult.of(results, params.page(), params.size(), totalCount);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(results);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(results, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            CategoryPresetPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(2L);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(results, params.page(), params.size(), totalCount);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            CategoryPresetSearchParams params = CategoryPresetQueryFixtures.searchParams(0, 20);
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();
            List<CategoryPresetResult> emptyResults = Collections.emptyList();
            long totalCount = 0L;
            CategoryPresetPageResult expected =
                    CategoryPresetPageResult.of(
                            emptyResults, params.page(), params.size(), totalCount);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyResults);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(emptyResults, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            CategoryPresetPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("판매채널 필터가 적용된 검색을 수행한다")
        void execute_WithSalesChannelFilter_FiltersResults() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            CategoryPresetSearchParams params =
                    CategoryPresetQueryFixtures.searchParams(salesChannelIds);
            CategoryPresetSearchCriteria criteria =
                    CategoryPresetFixtures.searchCriteriaWithSalesChannel(salesChannelIds);
            List<CategoryPresetResult> results =
                    List.of(CategoryPresetQueryFixtures.categoryPresetResult(1L, 1L, 1L));
            long totalCount = 1L;
            CategoryPresetPageResult expected =
                    CategoryPresetPageResult.of(results, params.page(), params.size(), totalCount);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(results);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(results, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            CategoryPresetPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }
    }
}
