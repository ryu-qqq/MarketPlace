package com.ryuqq.marketplace.application.saleschannelbrand.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.ryuqq.marketplace.application.saleschannelbrand.SalesChannelBrandQueryFixtures;
import com.ryuqq.marketplace.application.saleschannelbrand.assembler.SalesChannelBrandAssembler;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.factory.SalesChannelBrandQueryFactory;
import com.ryuqq.marketplace.application.saleschannelbrand.manager.SalesChannelBrandReadManager;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
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
@DisplayName("SearchSalesChannelBrandByOffsetService 단위 테스트")
class SearchSalesChannelBrandByOffsetServiceTest {

    @InjectMocks private SearchSalesChannelBrandByOffsetService sut;

    @Mock private SalesChannelBrandReadManager readManager;
    @Mock private SalesChannelBrandQueryFactory queryFactory;
    @Mock private SalesChannelBrandAssembler assembler;

    @Nested
    @DisplayName("execute() - 외부채널 브랜드 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 외부채널 브랜드 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            SalesChannelBrandSearchParams params =
                    SalesChannelBrandQueryFixtures.searchParams(0, 20);
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            List<SalesChannelBrand> brands =
                    List.of(
                            SalesChannelBrandFixtures.activeSalesChannelBrand(1L),
                            SalesChannelBrandFixtures.activeSalesChannelBrand(2L));
            long totalElements = 2L;

            SalesChannelBrandPageResult expected =
                    SalesChannelBrandPageResult.of(
                            List.of(
                                    SalesChannelBrandQueryFixtures.salesChannelBrandResult(1L),
                                    SalesChannelBrandQueryFixtures.salesChannelBrandResult(2L)),
                            params.page(),
                            params.size(),
                            totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(brands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(brands, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelBrandPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).hasSize(2);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(brands, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            SalesChannelBrandSearchParams params =
                    SalesChannelBrandQueryFixtures.searchParams(0, 20);
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            List<SalesChannelBrand> emptyBrands = Collections.emptyList();
            long totalElements = 0L;

            SalesChannelBrandPageResult expected =
                    SalesChannelBrandPageResult.of(
                            Collections.emptyList(), params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyBrands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(
                            emptyBrands, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelBrandPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isEmpty();
        }

        @Test
        @DisplayName("판매채널 ID 필터가 적용된 검색을 수행한다")
        void execute_WithSalesChannelIdFilter_FiltersResults() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SalesChannelBrandSearchParams params =
                    SalesChannelBrandQueryFixtures.searchParams(salesChannelIds);
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            List<SalesChannelBrand> brands =
                    List.of(SalesChannelBrandFixtures.activeSalesChannelBrand(1L));
            long totalElements = 1L;

            SalesChannelBrandPageResult expected =
                    SalesChannelBrandPageResult.of(
                            List.of(SalesChannelBrandQueryFixtures.salesChannelBrandResult(1L)),
                            params.page(),
                            params.size(),
                            totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(brands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(brands, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SalesChannelBrandPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }
    }
}
